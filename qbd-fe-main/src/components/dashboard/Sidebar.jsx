import React from "react";
import Cookies from 'js-cookie';
import { useNavigate } from 'react-router-dom';
import { LayoutDashboard, AlertCircle, Truck, Droplet, LogOut } from "lucide-react";

const Sidebar = ({ activeSection, setActiveSection }) => {
  const navigate=useNavigate();

  const menuItems = [
    { id: "Inventory", label: "Blood Inventory", icon: <LayoutDashboard size={20} /> },
    { id: "Alerts", label: "Hospital Alerts", icon: <AlertCircle size={20} /> },
    { id: "Transfers", label: "Active Transfers", icon: <Truck size={20} /> },
  ];

  const handleLogout = () => { 
        Cookies.remove('jwtToken'); 
        navigate('/login'); 
    };

  return (
    <aside className="w-64 bg-white border-r border-gray-100 hidden md:flex flex-col justify-between shadow-sm z-10">
      
      {/* LOGO AREA */}
      <div className="p-6">
        <div className="flex items-center gap-2 mb-8">
          <div className="bg-rose-600 p-1.5 rounded-lg">
             <Droplet className="w-5 h-5 text-white fill-current" />
          </div>
          <span className="text-xl font-bold tracking-tight text-gray-900">
            Quick Blood<span className="text-rose-600"> Donate</span>
          </span>
        </div>

        {/* NAVIGATION LINKS */}
        <nav className="space-y-1">
          {menuItems.map((item) => (
            <button
              key={item.id}
              onClick={() => setActiveSection(item.id)}
              className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-medium transition-all duration-200
                ${
                  activeSection === item.id
                    ? "bg-rose-50 text-rose-700 shadow-sm ring-1 ring-rose-200"
                    : "text-gray-500 hover:bg-gray-50 hover:text-gray-900"
                }
              `}
            >
              {item.icon}
              {item.label}
            </button>
          ))}
        </nav>
      </div>

      {/* FOOTER AREA */}
      <div className="p-6 border-t border-gray-50">
        <button className="flex items-center gap-3 text-sm font-medium text-gray-500 hover:text-rose-600 transition-colors w-full" onClick={handleLogout}>
          <LogOut size={18} />
          Sign Out
        </button>
      </div>

    </aside>
  );
};

export default Sidebar;