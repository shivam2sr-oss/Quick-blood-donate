import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Cookies from 'js-cookie';
import apiInterceptor from '../../config/ApiInterceptor'; // Keep this ONLY for profile load if you don't have a UserService
import { 
  Activity, Plus, MapPin, LogOut, Loader2, Hospital, AlertOctagon 
} from 'lucide-react';
import HospitalApi from '../../services/HospitalService'; // ✅ Using your Service
import { toast } from 'react-toastify';

const HospitalDashboard = () => {
  const navigate = useNavigate();

  // --- STATE ---
  const [profile, setProfile] = useState(null);
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);

  // Form State
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({
    bloodGroup: 'A_POS',
    unitsNeeded: 1,
    urgency: 'NORMAL' // 'NORMAL' or 'CRITICAL'
  });

  // --- 1. LOAD DATA ---
  useEffect(() => {
    const init = async () => {
      try {
        // Fetch User Profile
        // (If you have a UserService, use that. Otherwise, direct call here is fine for now)
        const userRes = await apiInterceptor.get('/users/current');
        setProfile(userRes.data);
        
        // If user has an organization, fetch their request history
        if (userRes.data?.organization?.id) {
          fetchRequests(userRes.data.organization.id);
        }
      } catch (error) {
        console.error("Profile Load Error:", error);
        if (error.response?.status === 401) { 
            Cookies.remove('jwtToken'); 
            navigate('/login'); 
        }
      } finally {
        setLoading(false);
      }
    };
    init();
  }, [navigate]);

  const fetchRequests = async (hospitalId) => {
    // ✅ Using Service
    const data = await HospitalApi.getRequests(hospitalId);
    if (data) {
        setRequests(data);
    }
  };

  // --- 2. SUBMIT REQUEST ---
  const handleSubmit = async () => {
    // Validation: Ensure we know which hospital is making the request
    if (!profile?.organization?.id) {
        toast.error("Organization ID missing. Please login again.");
        return;
    }

    // Prepare the DTO
    // ⚠️ FIX: Use 'profile.organization.id', NOT 'formData.organization.id'
    const hospitalRequestCreateDTO = {
        hospitalId: profile.organization.id, 
        bloodGroup: formData.bloodGroup,
        unitsNeeded: parseInt(formData.unitsNeeded), // Ensure it's a number
        urgency: formData.urgency
    };

    // ✅ Using Service
    const result = await HospitalApi.submitRequest(hospitalRequestCreateDTO);
    
    // If result exists (Service returns data on success, nothing/undefined on error)
    if (result) {
        toast.success("Request Sent to District CBB!");
        setShowModal(false);
        // Reset form slightly if needed, or keep defaults
        setFormData(prev => ({ ...prev, unitsNeeded: 1, urgency: 'NORMAL' }));
        fetchRequests(profile.organization.id); // Refresh List
    }
  };

  // --- HELPER: FORMAT TEXT ---
  const formatBloodGroup = (bg) => bg ? bg.replace('_', ' ').replace('POS', '+').replace('NEG', '-') : bg;

  const handleLogout = () => { 
      Cookies.remove('jwtToken'); 
      navigate('/login'); 
  };

  if (loading) return <div className="flex h-screen items-center justify-center"><Loader2 className="animate-spin text-rose-600"/></div>;

  return (
    <div className="min-h-screen bg-gray-50 pb-12">
      
      {/* NAVBAR */}
      <nav className="bg-white border-b px-8 py-4 flex justify-between items-center sticky top-0 z-10">
        <div className="flex items-center gap-2 text-rose-600 font-bold text-xl">
          <Hospital className="fill-current"/> 
          <span className="text-gray-900">{profile?.organization?.name || "Hospital Dashboard"}</span>
        </div>
        <div className="flex items-center gap-6">
           <div className="hidden md:flex items-center gap-2 text-sm text-gray-500 bg-gray-100 px-3 py-1.5 rounded-full">
              <MapPin size={14}/> District: <span className="font-bold text-gray-800">{profile?.organization?.district || "Unknown"}</span>
           </div>
           <button onClick={handleLogout} className="text-gray-500 hover:text-rose-600"><LogOut size={20}/></button>
        </div>
      </nav>

      <div className="max-w-6xl mx-auto px-6 py-8">
        
        {/* HEADER AREA */}
        <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-8">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Blood Requests</h1>
            <p className="text-gray-500">Manage supply requests to your District Blood Bank.</p>
          </div>
          <button 
            onClick={() => setShowModal(true)}
            className="bg-rose-600 text-white px-6 py-3 rounded-xl font-bold flex items-center gap-2 hover:bg-rose-700 shadow-lg shadow-rose-200 transition-all"
          >
            <Plus size={20}/> New Request
          </button>
        </div>

        {/* REQUESTS TABLE */}
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
          {requests.length > 0 ? (
            <table className="w-full text-left">
              <thead className="bg-gray-50 text-xs uppercase text-gray-500 font-semibold">
                <tr>
                  <th className="p-5">Request ID</th>
                  <th className="p-5">Blood Group</th>
                  <th className="p-5">Urgency</th>
                  <th className="p-5">Status</th>
                  <th className="p-5 text-right">Date</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100 text-sm">
                {requests.map((req) => (
                  <tr key={req.id} className="hover:bg-gray-50 transition-colors">
                    <td className="p-5 font-bold text-gray-900">#{req.id}</td>
                    <td className="p-5">
                        <span className="font-black text-gray-800 bg-gray-100 px-2 py-1 rounded">
                          {formatBloodGroup(req.bloodGroup)}
                        </span>
                        <span className="ml-2 text-gray-500">x {req.unitsNeeded} Units</span>
                    </td>
                    <td className="p-5">
                        {req.urgency === 'CRITICAL' ? (
                          <span className="flex items-center gap-1 text-red-600 font-bold text-xs bg-red-50 px-2 py-1 rounded w-fit border border-red-100">
                            <AlertOctagon size={12}/> CRITICAL
                          </span>
                        ) : (
                          <span className="text-gray-500 font-medium text-xs bg-gray-100 px-2 py-1 rounded w-fit">Normal</span>
                        )}
                    </td>
                    <td className="p-5">
                      <span className={`px-3 py-1 rounded-full text-xs font-bold border ${
                        req.status === 'PENDING' ? 'bg-amber-50 text-amber-600 border-amber-100' :
                        req.status === 'DISPATCHED' ? 'bg-blue-50 text-blue-600 border-blue-100' :
                        'bg-emerald-50 text-emerald-600 border-emerald-100'
                      }`}>
                        {req.status}
                      </span>
                    </td>
                    <td className="p-5 text-right text-gray-400 text-xs">
                      {new Date(req.requestDate).toLocaleDateString()}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
             <div className="p-16 text-center text-gray-400 flex flex-col items-center">
                <Activity size={48} className="mb-4 opacity-20"/>
                <p>No requests found. Create one to get started.</p>
             </div>
          )}
        </div>
      </div>

      {/* --- NEW REQUEST MODAL --- */}
      {showModal && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50 p-4 animate-in fade-in">
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden">
             
             {/* Modal Header */}
             <div className="bg-rose-600 p-6 flex justify-between items-center text-white">
                <h3 className="font-bold text-lg flex items-center gap-2"><Activity size={20}/> Request Blood Supply</h3>
                <button onClick={() => setShowModal(false)} className="hover:bg-rose-700 p-1 rounded-full"><span className="text-2xl">&times;</span></button>
             </div>

             {/* Modal Body */}
             <div className="p-6 space-y-5">
                
                {/* 1. Blood Group Select */}
                <div>
                   <label className="block text-xs font-bold text-gray-500 uppercase mb-2">Required Blood Group</label>
                   <select 
                     className="w-full border border-gray-300 p-3 rounded-xl font-bold text-gray-700 outline-none focus:ring-2 focus:ring-rose-500"
                     value={formData.bloodGroup}
                     onChange={(e) => setFormData({...formData, bloodGroup: e.target.value})}
                   >
                     {['A_POS','A_NEG','B_POS','B_NEG','O_POS','O_NEG','AB_POS','AB_NEG'].map(bg => (
                       <option key={bg} value={bg}>{formatBloodGroup(bg)}</option>
                     ))}
                   </select>
                </div>

                {/* 2. Units & Urgency */}
                <div className="grid grid-cols-2 gap-4">
                   <div>
                     <label className="block text-xs font-bold text-gray-500 uppercase mb-2">Units (Bags)</label>
                     <input 
                       type="number" 
                       min="1" max="50"
                       className="w-full border border-gray-300 p-3 rounded-xl font-bold outline-none focus:ring-2 focus:ring-rose-500"
                       value={formData.unitsNeeded}
                       onChange={(e) => setFormData({...formData, unitsNeeded: e.target.value})}
                     />
                   </div>
                   
                   <div>
                     <label className="block text-xs font-bold text-gray-500 uppercase mb-2">Urgency Level</label>
                     <div className="flex gap-2">
                        <button 
                          onClick={() => setFormData({...formData, urgency: 'NORMAL'})}
                          className={`flex-1 py-3 rounded-xl text-xs font-bold border transition-all ${formData.urgency === 'NORMAL' ? 'bg-gray-800 text-white border-gray-800' : 'bg-white text-gray-500 border-gray-200'}`}
                        >
                          Normal
                        </button>
                        <button 
                          onClick={() => setFormData({...formData, urgency: 'CRITICAL'})}
                          className={`flex-1 py-3 rounded-xl text-xs font-bold border transition-all ${formData.urgency === 'CRITICAL' ? 'bg-red-600 text-white border-red-600' : 'bg-white text-red-500 border-gray-200'}`}
                        >
                          Critical
                        </button>
                     </div>
                   </div>
                </div>

                {/* Submit Button */}
                <button 
                  onClick={handleSubmit}
                  className="w-full bg-rose-600 text-white py-4 rounded-xl font-bold text-lg hover:bg-rose-700 shadow-lg shadow-rose-200 mt-4 transition-transform active:scale-95"
                >
                  Send Request
                </button>

             </div>
          </div>
        </div>
      )}

    </div>
  );
};

export default HospitalDashboard;