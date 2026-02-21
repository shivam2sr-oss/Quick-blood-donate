import React from "react";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";

// Imports
import LandingPage from "../pages/LandingPage";
import Login from "../pages/Login";
import SignUp from "../pages/SignUp";

// Dashboard Imports
import DonorDashboard from "../pages/dashboards/DonorDashboard";
import NodeDashboard from "../pages/dashboards/NodeDashboard";
import NodeCamps from "../pages/dashboards/NodeCamps"; // ✅ NEW IMPORT

// Security Import
import ProtectedRoute from "../config/ProtectedRoute";
import CbbDashboard from "../pages/dashboards/CbbDashboard";
import HospitalDashboard from "../pages/dashboards/HospitalDashboard";

const RoutesComponent = () => {
  return (
    <Router>
      <Routes>
        {/* Public Routes */}
        <Route path="/" element={<LandingPage />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<SignUp />} />
        
        <Route
          path="/unauthorized"
          element={
            <div className="p-10 text-center">
              <h1 className="text-3xl font-bold text-red-600">403 - Unauthorized</h1>
              <p>You do not have permission to view this page.</p>
            </div>
          }
        />

        {/* --- PROTECTED ROUTES --- */}

        {/* 1. NODE STAFF ROUTES */}
        <Route
          path="/node/dashboard"
          element={
            <ProtectedRoute
              element={NodeDashboard}
              allowedRoles={["ROLE_NODE_STAFF"]}
            />
          }
        />
        <Route
          path="/hospital/dashboard"
          element={
            <ProtectedRoute
              element={HospitalDashboard}
              allowedRoles={["ROLE_HOSPITAL_STAFF"]}
            />
          }
        />
        
        {/* ✅ NEW ROUTE: Node Camps Management */}
        <Route
          path="/node/camps"
          element={
            <ProtectedRoute
              element={NodeCamps}
              allowedRoles={["ROLE_NODE_STAFF"]}
            />
          }
        />

        {/* 2. DONOR ROUTES */}
        <Route
          path="/donor/dashboard"
          element={
            <ProtectedRoute
              element={DonorDashboard}
              allowedRoles={["DONOR", "ROLE_DONOR"]}
            />
          }
        />
        <Route
          path="/cbb/dashboard"
          element={
            <ProtectedRoute
              element={CbbDashboard}
              allowedRoles={["ROLE_CBB_STAFF"]}
            />
          }
        />

        {/* Fallback */}
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </Router>
  );
};

export default RoutesComponent;