import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Cookies from 'js-cookie';
import apiInterceptor from '../../config/ApiInterceptor'; 
import { 
  Heart, Calendar, MapPin, LogOut, 
  Clock, CheckCircle2, Droplet, X, Loader2, Ticket 
} from 'lucide-react';
import OrganizationApi from '../../services/OrganizationService';
import { toast } from 'react-toastify';

const DonorDashboard = () => {
  const navigate = useNavigate();
  
  // --- STATE ---
  const [profile, setProfile] = useState(null);
  const [donations, setDonations] = useState([]);
  const [nodes, setNodes] = useState([]);
  const [loading, setLoading] = useState(true);
  
  // Modal State
  const [showModal, setShowModal] = useState(false);
  const [selectedNode, setSelectedNode] = useState('');
  const [selectedDate, setSelectedDate] = useState(''); 
  const [requestLoading, setRequestLoading] = useState(false);

  // --- 1. LOAD DATA ---
  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      // 1. Get Profile
      const profileRes = await apiInterceptor.get('/users/current');
      setProfile(profileRes.data);

      // 2. Get History
      if(profileRes.data.id) {
         try {
           const historyRes = await apiInterceptor.get(`/donations/donor/${profileRes.data.id}`);
           setDonations(historyRes.data);
         } catch(e) { console.warn("History load failed", e); }
      }
    } catch (err) {
      if (err.response?.status === 401) { Cookies.remove('jwtToken'); navigate('/login'); }
    } finally {
      setLoading(false);
    }
  };

  // --- 2. OPEN MODAL ---
  const handleDonateClick = async () => {
    setShowModal(true);
    // Default date to tomorrow
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    setSelectedDate(tomorrow.toISOString().split('T')[0]);

    try {
      const city = profile?.address || ""; 
      const response = await OrganizationApi.fetchOrganizationBasedOnCity(city);
      console.log(response);
      
    //   const res = await apiInterceptor.get(city ? `/organizations/nodes?city=${city}` : '/organizations/nodes');
      setNodes(response);
    } catch (error) {
    //   alert("Error loading locations.");
    }
  };

  // --- 3. SUBMIT BOOKING ---
  const handleSubmitRequest = async () => {
    if(!selectedNode) return alert("Please select a location");
    if(!selectedDate) return alert("Please select a date");

    setRequestLoading(true);
    try {
      const payload = { 
        donorId: profile.id, 
        nodeId: selectedNode,
        preferredDate: selectedDate // ✅ Sends the Date to Backend
      };
      
      await apiInterceptor.post('/donations/create', payload);
      
      toast.success("Appointment Requested! Wait for approval.");
      setShowModal(false);
      loadDashboardData(); 
    } catch (error) {
      alert(error.response?.data?.message || "Failed to book appointment");
    } finally {
      setRequestLoading(false);
    }
  };

  const handleLogout = () => { Cookies.remove('jwtToken'); navigate('/login'); };

  if (loading) return <div className="flex h-screen items-center justify-center"><Loader2 className="animate-spin text-rose-600"/></div>;

  // Check for Active Appointment (APPROVED status)
  const activeAppointment = donations.find(d => d.status === 'APPROVED' || d.status === 'PENDING');
  const approvedAppointment = donations.find(d => d.status === 'APPROVED');

  return (
    <div className="min-h-screen bg-gray-50 pb-12">
      {/* Navbar */}
      <nav className="bg-white border-b px-6 py-4 flex justify-between items-center sticky top-0 z-10">
        <div className="flex items-center gap-2 text-rose-600 font-bold text-xl"><Heart className="fill-current"/> BloodFlow</div>
        <button onClick={handleLogout} className="text-gray-500 hover:text-rose-600"><LogOut size={20}/></button>
      </nav>

      <div className="max-w-5xl mx-auto px-6 py-8">
        
        {/* --- APPOINTMENT PASS (Only if Approved) --- */}
        {approvedAppointment && (
          <div className="mb-8 bg-gradient-to-r from-emerald-500 to-teal-600 rounded-2xl p-6 text-white shadow-lg relative overflow-hidden animate-in fade-in slide-in-from-top-4">
            <div className="relative z-10 flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
              <div>
                <h2 className="text-2xl font-bold flex items-center gap-2"><Ticket className="fill-white/20"/> Appointment Confirmed</h2>
                <p className="text-emerald-100 mt-1">Please present this pass at the center.</p>
                <div className="mt-4 flex gap-8">
                  <div><p className="text-xs text-emerald-100 uppercase font-bold">Location</p><p className="font-bold text-lg">{approvedAppointment.nodeName}</p></div>
                  <div><p className="text-xs text-emerald-100 uppercase font-bold">Date</p><p className="font-bold text-lg">{approvedAppointment.donationDate}</p></div>
                </div>
              </div>
              <div className="bg-white/20 p-4 rounded-xl backdrop-blur-sm text-center min-w-[100px]">
                <p className="text-xs font-bold uppercase">Status</p>
                <p className="font-bold text-xl">APPROVED</p>
              </div>
            </div>
            {/* Background decoration */}
            <div className="absolute -right-10 -bottom-10 w-48 h-48 bg-white/10 rounded-full blur-3xl"></div>
          </div>
        )}

        {/* Header */}
        <div className="flex justify-between items-center mb-8">
          <div><h1 className="text-2xl font-bold">Hello, {profile?.fullName}</h1><p className="text-gray-500">Ready to save a life today?</p></div>
          
          {/* Donate Button Logic */}
          {!activeAppointment ? (
             profile?.eligible ? (
               <button onClick={handleDonateClick} className="bg-rose-600 hover:bg-rose-700 text-white px-6 py-3 rounded-xl font-bold flex items-center gap-2 shadow-lg shadow-rose-200 hover:scale-105 transition-transform">
                 <Calendar size={20}/> Book Appointment
               </button>
             ) : (
               <div className="bg-orange-100 text-orange-700 px-4 py-2 rounded-lg font-bold flex items-center gap-2"><Clock size={16}/> Cooling Period</div>
             )
          ) : (
            // Show status badge if pending
             !approvedAppointment && (
              <div className="bg-blue-50 text-blue-700 px-4 py-2 rounded-lg font-bold border border-blue-200">
                 Request Pending...
              </div>
             )
          )}
        </div>

        {/* History Table */}
        <div className="bg-white rounded-2xl border overflow-hidden shadow-sm">
          <div className="px-6 py-5 border-b font-bold text-gray-800">Donation History</div>
          <table className="w-full text-sm text-left">
            <thead className="bg-gray-50 text-gray-500 uppercase text-xs"><tr><th className="px-6 py-4">Date</th><th className="px-6 py-4">Location</th><th className="px-6 py-4">Status</th></tr></thead>
            <tbody className="divide-y divide-gray-100">
              {donations.map((d) => (
                <tr key={d.id}>
                  <td className="px-6 py-4">{d.donationDate}</td>
                  <td className="px-6 py-4">{d.nodeName}</td>
                  <td className="px-6 py-4">
                    <span className={`px-2 py-1 rounded text-xs font-bold ${
                      d.status === 'APPROVED' ? 'bg-emerald-100 text-emerald-700' : 
                      d.status === 'COMPLETED' ? 'bg-purple-100 text-purple-700' : 
                      d.status === 'REJECTED' ? 'bg-red-50 text-red-700' : 
                      'bg-blue-50 text-blue-700'}`}>
                      {d.status}
                    </span>
                  </td>
                </tr>
              ))}
              {donations.length === 0 && <tr><td colSpan="3" className="p-8 text-center text-gray-400">No history found.</td></tr>}
            </tbody>
          </table>
        </div>
      </div>

      {/* --- BOOKING MODAL --- */}
      {showModal && (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden">
            <div className="bg-rose-600 p-6 flex justify-between items-center text-white">
              <h3 className="font-bold text-lg flex items-center gap-2"><Calendar size={20}/> Book Appointment</h3>
              <button onClick={() => setShowModal(false)} className="hover:bg-rose-700 p-1 rounded-full"><X size={20}/></button>
            </div>
            <div className="p-6">
              
              <div className="mb-4">
                <label className="block text-sm font-bold text-gray-700 mb-2">Select Date</label>
                <input 
                  type="date" 
                  min={new Date().toISOString().split('T')[0]} 
                  value={selectedDate}
                  onChange={(e) => setSelectedDate(e.target.value)}
                  className="w-full border p-3 rounded-xl focus:ring-2 focus:ring-rose-500 outline-none"
                />
              </div>

              <label className="block text-sm font-bold text-gray-700 mb-2">Select Center</label>
              <div className="space-y-3 max-h-48 overflow-y-auto mb-6 pr-2">
                {nodes.map((node) => (
                  <div 
                    key={node.id} 
                    onClick={() => setSelectedNode(node.id)}
                    className={`p-4 rounded-xl border cursor-pointer flex justify-between items-center transition-all ${selectedNode === node.id ? 'border-rose-500 bg-rose-50 ring-1 ring-rose-500' : 'border-gray-200 hover:border-rose-300'}`}
                  >
                    <div><p className="font-bold text-gray-900">{node.name}</p><p className="text-xs text-gray-500">{node.city}</p></div>
                    {selectedNode === node.id && <CheckCircle2 className="text-rose-600" size={20}/>}
                  </div>
                ))}
              </div>

              <button 
                onClick={handleSubmitRequest}
                disabled={!selectedNode || !selectedDate || requestLoading}
                className="w-full bg-gray-900 text-white py-3 rounded-xl font-bold hover:bg-black disabled:opacity-50"
              >
                {requestLoading ? "Processing..." : "Confirm Booking"}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default DonorDashboard;