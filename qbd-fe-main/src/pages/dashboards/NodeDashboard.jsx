import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Cookies from 'js-cookie';
import apiInterceptor from '../../config/ApiInterceptor';
import { 
  Syringe, LogOut, CheckCircle2, 
  XCircle, User, Loader2, Save, Calendar, FileText, Tent 
} from 'lucide-react';

const NodeDashboard = () => {
  const navigate = useNavigate();
  
  // State
  const [staffProfile, setStaffProfile] = useState(null);
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('PENDING'); // 'PENDING' | 'APPROVED'
  
  // Modal State
  const [showCompleteModal, setShowCompleteModal] = useState(false);
  const [selectedRequest, setSelectedRequest] = useState(null);
  const [unitsCollected, setUnitsCollected] = useState(350);

  // 1. Initial Load: Get Staff Profile
  useEffect(() => {
    loadStaffProfile();
  }, []);

  // 2. Tab Change Listener: Fetch requests when tab changes or profile loads
  useEffect(() => {
    if (staffProfile?.organization?.id) {
      fetchRequests(staffProfile.organization.id, activeTab);
    }
  }, [staffProfile, activeTab]);

  const loadStaffProfile = async () => {
    try {
      const userRes = await apiInterceptor.get('/users/current');
      setStaffProfile(userRes.data);
    } catch (error) {
      if (error.response?.status === 401) { Cookies.remove('jwtToken'); navigate('/login'); }
    } finally {
      setLoading(false);
    }
  };

  const fetchRequests = async (nodeId, status) => {
    try {
      const res = await apiInterceptor.get(`/donations/node/${nodeId}/${status}`);
      setRequests(res.data);
    } catch (e) {
      console.error("Failed to fetch requests", e);
      setRequests([]);
    }
  };

  // --- ACTIONS ---
  const handleDecision = async (requestId, isApproved) => {
    const remarks = isApproved ? "Medical Check Passed" : prompt("Enter Rejection Reason:");
    if (!isApproved && !remarks) return;

    try {
      await apiInterceptor.put('/donations/approve', {
        requestId,
        nodeId: staffProfile.organization.id,
        approve: isApproved,
        medicalRemarks: remarks || "Approved"
      });
      fetchRequests(staffProfile.organization.id, activeTab); // Refresh list
      alert(isApproved ? "Donor Approved!" : "Request Rejected");
    } catch (error) {
      alert("Action Failed: " + (error.response?.data?.message || "Error"));
    }
  };

  const handleComplete = async () => {
    try {
      await apiInterceptor.put('/donations/complete', {
        requestId: selectedRequest.id,
        unitsCollected: parseInt(unitsCollected)
      });
      setShowCompleteModal(false);
      fetchRequests(staffProfile.organization.id, activeTab); // Refresh list
      alert("Blood Collected Successfully!");
    } catch (error) {
      alert("Completion Failed");
    }
  };

  const handleLogout = () => { Cookies.remove('jwtToken'); navigate('/login'); };

  if (loading) return <div className="flex h-screen items-center justify-center"><Loader2 className="animate-spin text-rose-600"/></div>;

  return (
    <div className="min-h-screen bg-gray-50 pb-12">
      {/* ✅ NAVBAR UPDATED WITH CAMPS BUTTON */}
      <nav className="bg-white border-b px-6 py-4 flex justify-between items-center sticky top-0 z-10">
        <div className="flex items-center gap-2 text-rose-600 font-bold text-xl">
          <Syringe className="fill-current" /> 
          <span className="text-gray-900">{staffProfile?.organization?.name || "Blood Bank"}</span>
        </div>
        
        <div className="flex items-center gap-4">
           {/* Manage Camps Button */}
           <button 
             onClick={() => navigate('/node/camps')}
             className="hidden sm:flex items-center gap-2 text-gray-500 hover:text-rose-600 font-bold text-sm transition-colors mr-2"
           >
             <Tent size={18}/> Manage Camps
           </button>

           <span className="text-sm text-gray-500 hidden sm:block border-l pl-4 border-gray-300">
             Staff: {staffProfile?.fullName}
           </span>
           
           <button onClick={handleLogout} className="text-gray-500 hover:text-rose-600">
             <LogOut size={18}/>
           </button>
        </div>
      </nav>

      <div className="max-w-6xl mx-auto px-6 py-8">
        <div className="mb-6">
          <h1 className="text-2xl font-bold text-gray-900">Incoming Donors</h1>
          <p className="text-gray-500">Manage daily collection queue.</p>
        </div>

        {/* --- TABS --- */}
        <div className="flex gap-6 mb-6 border-b border-gray-200">
          <button 
            onClick={() => setActiveTab('PENDING')}
            className={`pb-3 px-2 font-bold text-sm border-b-2 transition-all ${activeTab === 'PENDING' ? 'border-rose-600 text-rose-600' : 'border-transparent text-gray-500 hover:text-gray-700'}`}
          >
            Pending Approval
          </button>
          <button 
            onClick={() => setActiveTab('APPROVED')}
            className={`pb-3 px-2 font-bold text-sm border-b-2 transition-all ${activeTab === 'APPROVED' ? 'border-emerald-600 text-emerald-600' : 'border-transparent text-gray-500 hover:text-gray-700'}`}
          >
            Ready for Collection
          </button>
        </div>

        {/* --- TABLE --- */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
          {requests.length > 0 ? (
            <table className="w-full text-left">
              <thead className="bg-gray-50 text-xs uppercase text-gray-500 font-semibold border-b border-gray-100">
                <tr>
                  <th className="p-5">Donor Name</th>
                  <th className="p-5">Appointment Date</th>
                  <th className="p-5">Status</th>
                  <th className="p-5 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100 text-sm">
                {requests.map((req) => (
                  <tr key={req.id} className="hover:bg-gray-50 transition-colors">
                    <td className="p-5 font-bold text-gray-900 flex items-center gap-2">
                       <User size={16} className="text-gray-400"/> {req.donorName}
                    </td>
                    <td className="p-5 text-gray-500">
                      <div className="flex items-center gap-2"><Calendar size={14}/> {req.donationDate}</div>
                    </td>
                    <td className="p-5">
                      <span className={`px-2 py-1 rounded text-xs font-bold ${activeTab === 'APPROVED' ? 'bg-emerald-100 text-emerald-700' : 'bg-blue-50 text-blue-700'}`}>
                        {req.status}
                      </span>
                    </td>
                    <td className="p-5 text-right flex justify-end gap-2">
                      {activeTab === 'PENDING' ? (
                        <>
                          <button onClick={() => handleDecision(req.id, true)} className="flex items-center gap-1 bg-emerald-100 text-emerald-700 px-3 py-1.5 rounded-lg font-bold hover:bg-emerald-200 transition-colors"><CheckCircle2 size={14}/> Approve</button>
                          <button onClick={() => handleDecision(req.id, false)} className="flex items-center gap-1 bg-red-100 text-red-700 px-3 py-1.5 rounded-lg font-bold hover:bg-red-200 transition-colors"><XCircle size={14}/> Reject</button>
                        </>
                      ) : (
                        <button onClick={() => { setSelectedRequest(req); setShowCompleteModal(true); }} className="flex items-center gap-2 bg-gray-900 text-white px-4 py-2 rounded-lg font-bold hover:bg-black transition-colors shadow-lg shadow-gray-200">
                          <Syringe size={14}/> Collect Blood
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <div className="p-16 text-center text-gray-400 flex flex-col items-center">
              <FileText size={40} className="mb-4 opacity-20"/>
              <p>No requests found in this category.</p>
            </div>
          )}
        </div>
      </div>

      {/* --- COLLECTION MODAL --- */}
      {showCompleteModal && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-sm p-6 animate-in zoom-in-95 duration-200">
            <h3 className="font-bold text-lg mb-1 flex items-center gap-2"><Syringe className="text-rose-600"/> Complete Collection</h3>
            <p className="mb-6 text-sm text-gray-500">Donor: <span className="font-bold text-gray-900">{selectedRequest?.donorName}</span></p>
            
            <label className="text-xs font-bold text-gray-500 uppercase block mb-2">Volume Collected (ml)</label>
            <input 
              type="number" 
              value={unitsCollected} 
              onChange={(e) => setUnitsCollected(e.target.value)} 
              className="w-full border border-gray-300 p-3 rounded-lg mb-6 font-bold text-lg text-center focus:ring-2 focus:ring-rose-500 outline-none"
            />
            
            <div className="flex gap-3">
              <button onClick={() => setShowCompleteModal(false)} className="flex-1 py-3 text-gray-500 font-bold hover:bg-gray-100 rounded-xl transition-colors">Cancel</button>
              <button onClick={handleComplete} className="flex-1 bg-rose-600 text-white py-3 rounded-xl font-bold hover:bg-rose-700 shadow-lg shadow-rose-200 transition-colors flex justify-center items-center gap-2">
                <Save size={18}/> Save Record
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default NodeDashboard;