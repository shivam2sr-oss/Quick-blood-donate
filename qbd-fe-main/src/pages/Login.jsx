import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import AuthService from '../services/AuthService';
import { Droplet, Loader2, Lock, Mail } from 'lucide-react';
import Cookies from 'js-cookie';
import JwtUtils from '../utils/security/JwtUtils'; // ✅ Import JwtUtils
import { toast } from "react-toastify";

const Login = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({ email: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const loginDto = {
        "username": formData.email,
        "password": formData.password
      };

      const data = await AuthService.login(loginDto);
      console.log("Login Response:", data);

      if (data.jwtToken) {
        // 1. Save Token
        Cookies.set('jwtToken', data.jwtToken, { expires: 1 });
      
        // 2. Get User Role using JwtUtils (More reliable)
        const roles = JwtUtils.getUserRoles(); 
        const userRole = roles[0]; // Get the first role

        console.log("Logged in as Role:", userRole);

        // 3. Define Redirection Map
        // We handle both "DONOR" and "ROLE_DONOR" formats here to be safe
        const dashboardMap = {
          'ADMIN': '/admin/dashboard',
          'ROLE_ADMIN': '/admin/dashboard',
          
          'CBB_STAFF': '/cbb/dashboard',
          'ROLE_CBB_STAFF': '/cbb/dashboard',
          
          'NODE_STAFF': '/node/dashboard',
          'ROLE_NODE_STAFF': '/node/dashboard',
          
          'HOSPITAL_STAFF': '/hospital/dashboard',
          'ROLE_HOSPITAL_STAFF': '/hospital/dashboard',
          
          'DONOR': '/donor/dashboard',
          'ROLE_DONOR': '/donor/dashboard'
        };
        
        toast.success("login successfull");
        const targetPath = dashboardMap[userRole] || '/';
        
        if (targetPath === '/') {
            console.warn("Role mismatch! Redirecting to home. Role was:", userRole);
        }

        console.log("Redirecting to:", targetPath);
        navigate(targetPath);

      } else {
        throw new Error("Token not found in response");
      }
      
    } catch (err) {
      console.error("Login Error:", err);
      setError('Invalid email or password.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-rose-50 to-slate-100 flex items-center justify-center p-4">
      <div className="bg-white w-full max-w-md rounded-2xl shadow-xl p-8 border border-white/50 backdrop-blur-sm">
        
        {/* Logo Area */}
        <div className="text-center mb-8">
          <div className="inline-flex bg-rose-600 p-3 rounded-xl shadow-lg shadow-rose-200 mb-4 transform rotate-3">
            <Droplet className="w-8 h-8 text-white fill-current" />
          </div>
          <h1 className="text-2xl font-bold text-gray-900 tracking-tight">Welcome Back</h1>
          <p className="text-gray-500 text-sm mt-2">Sign in to access the BloodFlow network</p>
        </div>

        {/* Error Message */}
        {error && (
          <div className="bg-red-50 text-red-600 text-sm p-3 rounded-lg border border-red-100 text-center mb-6 animate-pulse">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-5">
          <div className="space-y-1">
            <label className="text-xs font-semibold text-gray-500 uppercase ml-1">Email Address</label>
            <div className="relative">
              <Mail className="absolute left-3 top-3.5 text-gray-400" size={18} />
              <input
                type="email"
                name="email"
                required
                className="w-full pl-10 pr-4 py-3 rounded-lg border border-gray-200 focus:border-rose-500 focus:ring-2 focus:ring-rose-200 outline-none transition-all bg-gray-50 focus:bg-white"
                placeholder="name@example.com"
                value={formData.email}
                onChange={handleChange}
              />
            </div>
          </div>

          <div className="space-y-1">
            <label className="text-xs font-semibold text-gray-500 uppercase ml-1">Password</label>
            <div className="relative">
              <Lock className="absolute left-3 top-3.5 text-gray-400" size={18} />
              <input
                type="password"
                name="password"
                required
                className="w-full pl-10 pr-4 py-3 rounded-lg border border-gray-200 focus:border-rose-500 focus:ring-2 focus:ring-rose-200 outline-none transition-all bg-gray-50 focus:bg-white"
                placeholder="••••••••"
                value={formData.password}
                onChange={handleChange}
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-rose-600 hover:bg-rose-700 text-white font-bold py-3.5 rounded-lg shadow-lg shadow-rose-200 transition-all active:scale-[0.98] flex justify-center items-center gap-2"
          >
            {loading ? <Loader2 className="animate-spin" size={20}/> : "Sign In"}
          </button>
        </form>

        <p className="text-center text-sm text-gray-500 mt-8">
          Don't have an account?{' '}
          <Link to="/signup" className="text-rose-600 font-bold hover:underline">
            Register Now
          </Link>
        </p>
      </div>
    </div>
  );
};

export default Login;