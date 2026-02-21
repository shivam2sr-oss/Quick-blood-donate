import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Cookies from 'js-cookie';
import apiInterceptor from '../../config/ApiInterceptor';
import { 
  Tent, Calendar, MapPin, Plus, Save, 
  Loader2, LogOut, LayoutDashboard 
} from 'lucide-react';

const NodeCamps = () => {
  const navigate = useNavigate();
  
  // State
  const [profile, setProfile] = useState(null);
  const [camps, setCamps] = useState([]);
  const [loading, setLoading] = useState(true);
  
  // Create Modal State
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [newCamp, setNewCamp] = useState({
    campName: '', address: '', campDate: '', startTime: '', endTime: '', projectedUnits: ''
  });

  // Update Modal State
  const [showUpdateModal, setShowUpdateModal] = useState(false);
  const [selectedCamp, setSelectedCamp] = useState(null);
  const [actualUnits, setActualUnits] = useState('');

  // 1. Initial Load
  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      // Get Profile to get Node ID
      const userRes = await apiInterceptor.get('/users/current');
      setProfile(userRes.data);
      
      if(userRes.data?.organization?.id) {
         fetchCamps(userRes.data.organization.id);
      }
    } catch (error) {
      if (error.response?.status === 401) { Cookies.remove('jwtToken'); navigate('/login'); }
    } finally {
      setLoading(false);
    }
  };

  const fetchCamps = async (nodeId) => {
    try {
      const res = await apiInterceptor.get(`/donation-camps/organization/${nodeId}`);
      setCamps(res.data);
    } catch (e) {
      console.error("Failed to load camps", e);
    }
  };

  // --- ACTIONS ---

  const handleCreate = async () => {
    try {
      const payload = { ...newCamp, organizationId: profile.organization.id };
      await apiInterceptor.post('/donation-camps', payload);
      setShowCreateModal(false);
      fetchCamps(profile.organization.id); // Refresh
      alert("Camp Scheduled Successfully!");
    } catch (error) {
      alert("Failed to create camp.");
    }
  };

  const handleUpdateUnits = async () => {
    try {
      await apiInterceptor.put(`/donation-camps/${selectedCamp.id}/units?unitsCollected=${actualUnits}`);
      setShowUpdateModal(false);
      fetchCamps(profile.organization.id); // Refresh
      alert("Units Updated!");
    } catch (error) {
      alert("Failed to update units.");
    }
  };

  const handleLogout = () => { Cookies.remove('jwtToken'); navigate('/login'); };

  if (loading) return <div className="flex h-screen items-center justify-center"><Loader2 className="animate-spin text-rose-600"/></div>;

  return (
    <div className="min-h-screen bg-gray-50 pb-12">
      {/* Navbar */}
      <nav className="bg-white border-b px-6 py-4 flex justify-between items-center sticky top-0 z-10">
        <div className="flex items-center gap-2 text-rose-600 font-bold text-xl">
          <Tent className="fill-current"/> <span>Camp Manager</span>
        </div>
        <div className="flex gap-4">
          <button onClick={() => navigate('/node/dashboard')} className="text-gray-500 hover:text-rose-600 flex items-center gap-1 font-bold text-sm">
             <LayoutDashboard size={18}/> Dashboard
          </button>
          <button onClick={handleLogout} className="text-gray-500 hover:text-rose-600"><LogOut size={18}/></button>
        </div>
      </nav>

      <div className="max-w-6xl mx-auto px-6 py-8">
        
        <div className="flex justify-between items-center mb-8">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Donation Camps</h1>
            <p className="text-gray-500">Organize and manage blood donation drives.</p>
          </div>
          <button 
            onClick={() => setShowCreateModal(true)}
            className="bg-rose-600 text-white px-5 py-2.5 rounded-xl font-bold flex items-center gap-2 hover:bg-rose-700 shadow-lg shadow-rose-200 transition-all"
          >
            <Plus size={20}/> Schedule Camp
          </button>
        </div>

        {/* --- CAMPS GRID --- */}
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
          {camps.map(camp => (
            <div key={camp.id} className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm hover:shadow-md transition-shadow">
               <div className="flex justify-between items-start mb-4">
                  <div className="bg-rose-50 p-3 rounded-xl"><Tent className="text-rose-600" size={24}/></div>
                  <span className={`px-2 py-1 rounded text-xs font-bold ${camp.actualUnitsCollected > 0 ? 'bg-emerald-100 text-emerald-700' : 'bg-blue-50 text-blue-700'}`}>
                    {camp.actualUnitsCollected > 0 ? 'Completed' : 'Scheduled'}
                  </span>
               </div>
               
               <h3 className="font-bold text-lg text-gray-900 mb-1">{camp.campName}</h3>
               <p className="text-sm text-gray-500 flex items-center gap-1 mb-4"><MapPin size={14}/> {camp.address}</p>
               
               <div className="space-y-2 text-sm text-gray-600 mb-6 bg-gray-50 p-4 rounded-xl">
                  <div className="flex justify-between"><span>Date:</span> <span className="font-bold text-gray-900">{camp.campDate}</span></div>
                  <div className="flex justify-between"><span>Time:</span> <span className="font-bold text-gray-900">{camp.startTime} - {camp.endTime}</span></div>
                  <div className="flex justify-between"><span>Goal:</span> <span className="font-bold text-gray-900">{camp.projectedUnits} Units</span></div>
                  {camp.actualUnitsCollected > 0 && (
                    <div className="flex justify-between text-emerald-600 pt-2 border-t"><span>Collected:</span> <span className="font-bold">{camp.actualUnitsCollected} Units</span></div>
                  )}
               </div>

               {/* Action Button: Only show if not completed yet */}
               {camp.actualUnitsCollected === 0 && (
                 <button 
                   onClick={() => { setSelectedCamp(camp); setShowUpdateModal(true); }}
                   className="w-full border border-gray-200 text-gray-600 font-bold py-2 rounded-lg hover:bg-gray-50 hover:text-gray-900 transition-colors"
                 >
                   Update Results
                 </button>
               )}
            </div>
          ))}
          {camps.length === 0 && <div className="col-span-full text-center p-12 text-gray-400">No camps scheduled yet.</div>}
        </div>

      </div>

      {/* --- CREATE MODAL --- */}
      {showCreateModal && (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-lg p-6 max-h-[90vh] overflow-y-auto">
             <h3 className="font-bold text-xl mb-6">Schedule New Camp</h3>
             <div className="space-y-4">
                <input placeholder="Camp Name (e.g. City College Drive)" className="w-full border p-3 rounded-lg" onChange={e => setNewCamp({...newCamp, campName: e.target.value})}/>
                <input placeholder="Address" className="w-full border p-3 rounded-lg" onChange={e => setNewCamp({...newCamp, address: e.target.value})}/>
                <div className="grid grid-cols-2 gap-4">
                   <input type="date" className="border p-3 rounded-lg" onChange={e => setNewCamp({...newCamp, campDate: e.target.value})}/>
                   <input type="number" placeholder="Target Units" className="border p-3 rounded-lg" onChange={e => setNewCamp({...newCamp, projectedUnits: e.target.value})}/>
                </div>
                <div className="grid grid-cols-2 gap-4">
                   <input type="time" className="border p-3 rounded-lg" onChange={e => setNewCamp({...newCamp, startTime: e.target.value})}/>
                   <input type="time" className="border p-3 rounded-lg" onChange={e => setNewCamp({...newCamp, endTime: e.target.value})}/>
                </div>
             </div>
             <div className="flex gap-3 mt-8">
                <button onClick={() => setShowCreateModal(false)} className="flex-1 py-3 font-bold text-gray-500">Cancel</button>
                <button onClick={handleCreate} className="flex-1 bg-rose-600 text-white py-3 rounded-xl font-bold hover:bg-rose-700">Schedule Event</button>
             </div>
          </div>
        </div>
      )}

      {/* --- UPDATE MODAL --- */}
      {showUpdateModal && (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50 p-4">
           <div className="bg-white rounded-2xl shadow-xl w-full max-w-sm p-6">
              <h3 className="font-bold text-lg mb-4">Camp Completed?</h3>
              <p className="mb-4 text-sm text-gray-500">Enter total blood units collected at <b>{selectedCamp?.campName}</b>.</p>
              <input type="number" placeholder="Total Units" className="w-full border p-3 rounded-lg text-center font-bold text-lg mb-6" onChange={e => setActualUnits(e.target.value)}/>
              <div className="flex gap-3">
                 <button onClick={() => setShowUpdateModal(false)} className="flex-1 py-3 font-bold text-gray-500">Cancel</button>
                 <button onClick={handleUpdateUnits} className="flex-1 bg-emerald-600 text-white py-3 rounded-xl font-bold hover:bg-emerald-700">Save Results</button>
              </div>
           </div>
        </div>
      )}
    </div>
  );
};

export default NodeCamps;