import React from "react";
import Sidebar from "../components/dashboard/Sidebar";
import Header from "../components/dashboard/Header";

const CbbDashboardLayout = ({ children, activeSection, setActiveSection }) => {
  return (
    <div className="flex h-screen bg-gray-50 font-sans text-gray-900 overflow-hidden">
      
      {/* LEFT SIDEBAR */}
      <Sidebar activeSection={activeSection} setActiveSection={setActiveSection} />

      {/* RIGHT MAIN AREA */}
      <div className="flex-1 flex flex-col min-w-0">
        <Header title={activeSection} />
        
        {/* SCROLLABLE CONTENT AREA */}
        <main className="flex-1 overflow-y-auto p-6 md:p-8">
          <div className="max-w-7xl mx-auto">
            {children}
          </div>
        </main>
      </div>

    </div>
  );
};

export default CbbDashboardLayout;