import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Cookies from 'js-cookie';
import apiInterceptor from '../../config/ApiInterceptor'; // Adjust path
import { Loader2 } from 'lucide-react';

// Import your components
// ✅ UPDATED IMPORTS based on your structure:
import Sidebar from '../../components/dashboard/Sidebar'; 
import Header from '../../components/dashboard/Header'; 

// Note: Ensure the file names match exactly (case-sensitive!)
import Inventory from '../../components/dashboard/Inventory/Inventory';
import Alerts from '../../components/dashboard/Alerts/Alerts';
import Transfers from '../../components/dashboard/Transfers/Transfers';

const CbbDashboard = () => {
  const navigate = useNavigate();
  const [activeSection, setActiveSection] = useState("Inventory");
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);

  // 1. Fetch Profile on Mount
  useEffect(() => {
    const loadProfile = async () => {
      try {
        const res = await apiInterceptor.get('/users/current');
        // console.log(res.data.profile?.organization);
        console.log(res.data.organization.id);
        
        setProfile(res.data);
      } catch (error) {
        if (error.response?.status === 401) {
          Cookies.remove('jwtToken');
          navigate('/login');
        }
      } finally {
        setLoading(false);
      }
    };
    loadProfile();
  }, [navigate]);

  

  if (loading) {
    return <div className="h-screen flex items-center justify-center"><Loader2 className="animate-spin text-rose-600" /></div>;
  }

  // Get the CBB Organization ID
  const cbbId = profile?.organization?.id;
  console.log(cbbId);
  
  return (
    <div className="flex h-screen bg-gray-50 font-sans">
      {/* LEFT SIDEBAR */}
      <Sidebar activeSection={activeSection} setActiveSection={setActiveSection} />

      {/* MAIN CONTENT AREA */}
      <div className="flex-1 flex flex-col overflow-hidden">
        
        {/* TOP HEADER */}
        <Header title={activeSection} user={profile} />

        {/* DYNAMIC CONTENT */}
        <main className="flex-1 overflow-y-auto p-8">
          {activeSection === "Inventory" && <Inventory cbbId={cbbId} />}
          {activeSection === "Transfers" && <Transfers cbbId={cbbId} />}
          {activeSection === "Alerts" && <Alerts />}
        </main>
      </div>
    </div>
  );
};

export default CbbDashboard;