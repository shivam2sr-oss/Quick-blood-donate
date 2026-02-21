import React from 'react';
import { Navigate } from 'react-router-dom';
import JwtUtils from '../utils/security/JwtUtils'; // ✅ Only use JwtUtils

const ProtectedRoute = ({ element: Component, allowedRoles }) => {
    
    // 1. Check Login Status
    if (!JwtUtils.isAuthenticated()) {
        return <Navigate to="/login" replace />;
    }

    // 2. Check Permissions (Logic moved here)
    const userRoles = JwtUtils.getUserRoles(); // ✅ Uses the function we just restored
    const hasPermission = userRoles.some(role => allowedRoles.includes(role));

    if (allowedRoles && !hasPermission) {
        return <Navigate to="/unauthorized" replace />;
    }

    // 3. Render Dashboard
    return <Component />;
};

export default ProtectedRoute;