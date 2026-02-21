import React from "react";
import { motion } from "framer-motion";
import { Activity, Truck, Heart, Droplet } from "lucide-react";
import { useNavigate } from 'react-router-dom';

// --- ANIMATION VARIANTS ---

// Pulse for the central hub
const heartBeat = {
  animate: {
    scale: [1, 1.1, 1],
    boxShadow: [
      "0px 0px 0px rgba(244, 63, 94, 0.4)",
      "0px 0px 20px rgba(244, 63, 94, 0.6)",
      "0px 0px 0px rgba(244, 63, 94, 0.4)",
    ],
  },
  transition: {
    duration: 2,
    repeat: Infinity,
    ease: "easeInOut",
  },
};

// Floating animation for satellite nodes
const float = (delay = 0) => ({
  animate: {
    y: [0, -10, 0],
  },
  transition: {
    duration: 5,
    delay: delay,
    repeat: Infinity,
    ease: "easeInOut",
  },
});

const LandingPage = () => {

  const navigate = useNavigate(); // 2. Initialize hook

  return (
    <div className="relative min-h-screen bg-white text-gray-800 overflow-hidden font-sans selection:bg-rose-100 selection:text-rose-600">
      
      {/* --- BACKGROUND DECORATION --- */}
      <div className="absolute inset-0 bg-[radial-gradient(ellipse_at_top_right,_var(--tw-gradient-stops))] from-rose-50 via-white to-slate-50 opacity-70"></div>
      
      {/* Decorative Grid */}
      <div className="absolute inset-0 opacity-[0.03] pointer-events-none" 
           style={{ backgroundImage: 'radial-gradient(#e11d48 1px, transparent 1px)', backgroundSize: '30px 30px' }}>
      </div>

      {/* --- NAVBAR --- */}
      <nav className="relative z-20 flex justify-between items-center px-6 md:px-16 py-6 backdrop-blur-sm bg-white/50 border-b border-rose-100/50 sticky top-0">
        <div className="flex items-center gap-2">
          <div className="bg-rose-600 p-1.5 rounded-lg">
            <Droplet className="w-5 h-5 text-white fill-current" />
          </div>
          <h1 className="text-xl font-bold tracking-tight text-gray-900">
            Quick Blood<span className="text-rose-600"> Donate</span>
          </h1>
        </div>
        
        <div className="hidden md:flex gap-8 text-sm font-medium text-gray-500">
          {['Donor', 'Hospital', 'Inventory', 'About'].map((item) => (
            <span key={item} className="hover:text-rose-600 cursor-pointer transition-colors duration-200">
              {item}
            </span>
          ))}
        </div>

        {/* LOGIN BUTTON */}
              <button 
                onClick={() => navigate('/login')} // 3. Navigate to Login
                className="text-sm font-semibold text-gray-600 hover:text-rose-600 transition-colors"
              >
                Log In
              </button>
      </nav>

      {/* --- HERO SECTION --- */}
      <section className="relative z-10 px-6 md:px-16 pt-12 pb-24 grid lg:grid-cols-2 gap-12 lg:gap-20 items-center max-w-7xl mx-auto">

        {/* LEFT CONTENT */}
        <motion.div
          initial={{ opacity: 0, x: -50 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ duration: 0.8, ease: "easeOut" }}
          className="space-y-8"
        >
          {/* Badge */}
          <motion.div 
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.2 }}
            className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-rose-50 border border-rose-100 shadow-sm"
          >
            <span className="relative flex h-2 w-2">
              <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-rose-400 opacity-75"></span>
              <span className="relative inline-flex rounded-full h-2 w-2 bg-rose-500"></span>
            </span>
            <span className="text-xs font-bold tracking-wide text-rose-600 uppercase">Live System Active</span>
          </motion.div>

          {/* Headlines */}
          <div className="space-y-4">
            <h2 className="text-4xl md:text-6xl lg:text-7xl font-extrabold leading-[1.1] tracking-tight text-gray-900">
              Every Drop <br />
              <span className="text-transparent bg-clip-text bg-gradient-to-r from-rose-600 to-indigo-600">
                Connects Life.
              </span>
            </h2>
            <p className="text-lg md:text-xl text-gray-500 leading-relaxed max-w-lg">
              “A centralized blood donation platform connecting donors, collection centers, and hospitals—fast, secure, and reliable.”
            </p>
          </div>

          {/* CTA Buttons */}
          <div className="flex flex-wrap gap-4 pt-4">
            <motion.button
              whileHover={{ scale: 1.02, translateY: -2 }}
              whileTap={{ scale: 0.98 }}
              className="bg-gray-900 text-white px-8 py-4 rounded-xl shadow-xl hover:shadow-2xl hover:bg-black transition-all font-medium flex items-center gap-2"
            >
              Find Blood Now <Activity className="w-4 h-4" />
            </motion.button>

            <motion.button
              whileHover={{ scale: 1.02, translateY: -2 }}
              whileTap={{ scale: 0.98 }}
              className="px-8 py-4 rounded-xl border border-gray-200 bg-white shadow-sm hover:border-rose-200 hover:text-rose-600 transition-all font-medium"
            >
              Register as Donor
            </motion.button>
          </div>
          
          {/* Trust Indicators */}
          <div className="pt-8 flex items-center gap-6 text-gray-400 text-sm">
            <div className="flex -space-x-3">
               {[1,2,3,4].map(i => (
                 <div key={i} className="w-8 h-8 rounded-full bg-gray-200 border-2 border-white"></div>
               ))}
            </div>
            <p>Trusted by 50+ Hospitals</p>
          </div>
        </motion.div>

        {/* RIGHT — DYNAMIC ECOSYSTEM VISUAL */}
        <div className="relative h-[500px] w-full flex items-center justify-center select-none hidden md:flex">
          
          {/* Background Glow */}
          <div className="absolute w-[400px] h-[400px] bg-rose-200/30 rounded-full blur-3xl animate-pulse"></div>

          {/* --- CSS-BASED ORBITAL PATHS --- */}
          {/* We use SVG for the paths to animate particles along them */}
          <svg className="absolute w-full h-full pointer-events-none" viewBox="0 0 500 500">
            <defs>
              <linearGradient id="lineGradient" x1="0%" y1="0%" x2="100%" y2="0%">
                <stop offset="0%" stopColor="#fda4af" stopOpacity="0.2" />
                <stop offset="50%" stopColor="#e11d48" stopOpacity="1" />
                <stop offset="100%" stopColor="#fda4af" stopOpacity="0.2" />
              </linearGradient>
            </defs>
            
            {/* Path 1: Donor (Top Left) to Center */}
            <path id="path1" d="M 120 120 Q 250 120 250 250" fill="none" stroke="url(#lineGradient)" strokeWidth="2" strokeDasharray="6 6" className="opacity-30" />
            
            {/* Path 2: Center to Hospital (Bottom Right) */}
            <path id="path2" d="M 250 250 Q 250 380 380 380" fill="none" stroke="url(#lineGradient)" strokeWidth="2" strokeDasharray="6 6" className="opacity-30" />

             {/* Path 3: Center to Logistics (Bottom Left) */}
             <path id="path3" d="M 250 250 Q 120 250 120 380" fill="none" stroke="url(#lineGradient)" strokeWidth="2" strokeDasharray="6 6" className="opacity-30" />
          </svg>

          {/* --- ANIMATED PARTICLES (BLOOD CELLS) --- */}
          <BloodParticle delay={0} path="path('M 120 120 Q 250 120 250 250')" />
          <BloodParticle delay={1.5} path="path('M 120 120 Q 250 120 250 250')" />
          
          <BloodParticle delay={1} path="path('M 250 250 Q 250 380 380 380')" color="bg-rose-500" />
          
          <BloodParticle delay={0.5} path="path('M 250 250 Q 120 250 120 380')" color="bg-indigo-500" />

          {/* --- CENTRAL HUB --- */}
          <motion.div
            className="relative z-20 w-32 h-32 bg-white rounded-full flex flex-col items-center justify-center shadow-2xl border-4 border-rose-50"
            {...heartBeat}
          >
            <div className="absolute inset-0 bg-rose-500 rounded-full opacity-10 blur-xl"></div>
            <Heart className="w-12 h-12 text-rose-500 fill-rose-500" />
            <span className="text-xs font-bold text-gray-800 mt-2">Central Bank</span>
            <span className="text-[10px] text-gray-400">Processing</span>
          </motion.div>

          {/* --- SATELLITE NODES --- */}
          
          {/* Node 1: Donor (Top Left) */}
          <NodeCard 
            icon={<Droplet className="w-6 h-6 text-white" />} 
            title="Active Donor" 
            subtitle="Donating Now"
            bg="bg-rose-500"
            position="top-[80px] left-[20px] lg:left-[80px]"
            delay={0}
          />

          {/* Node 2: Hospital (Bottom Right) */}
          <NodeCard 
            icon={<Activity className="w-6 h-6 text-indigo-600" />} 
            title="City Hospital" 
            subtitle="Requesting: O+"
            bg="bg-white border-indigo-100 text-indigo-600"
            position="bottom-[80px] right-[20px] lg:right-[80px]"
            delay={1}
            isAlert
          />

           {/* Node 3: Logistics (Bottom Left) */}
           <NodeCard 
            icon={<Truck className="w-6 h-6 text-emerald-600" />} 
            title="Transit Unit" 
            subtitle="Arriving in 10m"
            bg="bg-white border-emerald-100 text-emerald-600"
            position="bottom-[80px] left-[20px] lg:left-[80px]"
            delay={2}
          />
          
          {/* Node 4: Inventory (Top Right - Floating) */}
          <motion.div 
            className="absolute top-[80px] right-[40px] bg-white/80 backdrop-blur px-4 py-2 rounded-lg shadow-sm border border-gray-100"
            animate={{ y: [0, 15, 0] }}
            transition={{ duration: 8, repeat: Infinity }}
          >
            <div className="flex items-center gap-2">
                <div className="w-2 h-2 bg-green-500 rounded-full animate-pulse"></div>
                <span className="text-xs font-medium text-gray-600">System Healthy</span>
            </div>
          </motion.div>

        </div>
      </section>

      {/* FOOTER */}
      <footer className="relative z-10 bg-gray-900 text-gray-400 text-sm py-8 text-center border-t border-gray-800">
        Centralized Blood Bank Management System • CDAC Project
      </footer>
    </div>
  );
};

// --- SUB-COMPONENTS ---

const NodeCard = ({ icon, title, subtitle, bg, position, delay, isAlert }) => (
  <motion.div
    className={`absolute ${position} z-20 flex items-center gap-3 p-3 pr-6 rounded-2xl shadow-xl border border-gray-100 bg-white cursor-pointer hover:scale-105 transition-transform`}
    {...float(delay)}
  >
    <div className={`w-12 h-12 rounded-xl flex items-center justify-center shadow-sm ${bg.includes('bg-rose') ? 'bg-rose-500 shadow-rose-200' : 'bg-gray-50'}`}>
      {icon}
    </div>
    <div>
      <h3 className="text-sm font-bold text-gray-800">{title}</h3>
      <p className={`text-xs ${isAlert ? 'text-rose-500 font-semibold' : 'text-gray-500'}`}>{subtitle}</p>
    </div>
    {isAlert && (
      <span className="absolute -top-1 -right-1 flex h-3 w-3">
        <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-rose-400 opacity-75"></span>
        <span className="relative inline-flex rounded-full h-3 w-3 bg-rose-500"></span>
      </span>
    )}
  </motion.div>
);

const BloodParticle = ({ path, delay, color = "bg-rose-500" }) => (
    <div 
        className={`absolute w-3 h-3 ${color} rounded-full shadow-sm z-10`}
        style={{
            offsetPath: path,
            animation: `move 3s linear infinite`,
            animationDelay: `${delay}s`,
            opacity: 0 // Starts invisible until animation kicks in
        }}
    />
);

export default LandingPage;