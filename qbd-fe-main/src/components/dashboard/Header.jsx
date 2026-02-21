import React from "react";
import { Bell, Search } from "lucide-react";

const Header = ({ title }) => {
  return (
    <header className="h-20 bg-white/80 backdrop-blur-md border-b border-gray-100 flex items-center justify-between px-8 sticky top-0 z-10">
      
      {/* Page Title */}
      <div>
        <h2 className="text-2xl font-bold text-gray-800 tracking-tight">{title}</h2>
        <p className="text-xs text-gray-400 font-medium">Central Blood Bank • Admin View</p>
      </div>

      {/* Right Actions */}
      <div className="flex items-center gap-4">
        {/* Search Bar */}
        <div className="hidden md:flex items-center bg-gray-50 px-4 py-2.5 rounded-full border border-gray-100 focus-within:border-rose-200 focus-within:ring-2 focus-within:ring-rose-50 transition-all">
            <Search size={18} className="text-gray-400" />
            <input 
                type="text" 
                placeholder="Search..." 
                className="bg-transparent border-none outline-none text-sm ml-2 text-gray-600 w-48 placeholder:text-gray-400"
            />
        </div>

        {/* Notifications */}
        <button className="relative p-2.5 rounded-full bg-white border border-gray-100 text-gray-500 hover:bg-gray-50 hover:text-rose-600 transition-colors">
          <Bell size={20} />
          <span className="absolute top-2 right-2.5 w-2 h-2 bg-rose-500 rounded-full border border-white"></span>
        </button>

        {/* User Profile */}
        <div className="w-10 h-10 rounded-full bg-gradient-to-tr from-rose-100 to-rose-200 border-2 border-white shadow-sm flex items-center justify-center text-rose-700 font-bold text-sm">
          CB
        </div>
      </div>
    </header>
  );
};

export default Header;