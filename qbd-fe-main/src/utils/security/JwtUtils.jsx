// import { jwtDecode } from "jwt-decode";
// import Cookies from 'js-cookie';
// const getUserRoles = () => {
//     const token = Cookies.get('jwtToken');
//     if (token) {
//         const decodedToken = jwtDecode(token);
//         return decodedToken.roles || [];
//     }
//     return [];
// };

// const getUsername = () => {
//     const token = Cookies.get('jwtToken');
//     if (token) {
//         const decodedToken = jwtDecode(token);
//         return decodedToken.sub || null;
//     }
//     return null;
// };

// const hasRole = (userRoles) => getUserRoles().some(role => userRoles.includes(role));

// const isAuthenticated = () => {
//     const token = Cookies.get('jwtToken');
//     return !!token;
// };

// // Function to check if the token is expired
// const isTokenExpired = () => {
//     const token = Cookies.get('jwtToken');
//     if (token) {
//         try {
//             const { exp } = jwtDecode(token);
//             if (exp && Date.now() >= exp * 1000) {
//                 return true;
//             }
//         } catch (error) {
//             console.error('Failed to decode token:', error);
//             return true;
//         }
//     }
//     return false;
// };

// const clearCookies = () => {
//     Cookies.remove('jwtToken');
//     Cookies.remove('refreshJwtToken');
// }

// const JwtUtils = {
//     getUserRoles,
//     isAuthenticated,
//     getUsername,
//     isTokenExpired,
//     hasRole,
//     clearCookies
// }

// export default JwtUtils;

import Cookies from 'js-cookie';
import { jwtDecode } from 'jwt-decode';

const JwtUtils = {
  // 1. Check if user is logged in
  isAuthenticated: () => {
    const token = Cookies.get('jwtToken');
    return !!token && !JwtUtils.isTokenExpired();
  },

  // 2. Check expiration
  isTokenExpired: () => {
    const token = Cookies.get('jwtToken');
    if (!token) return true;
    try {
      const { exp } = jwtDecode(token);
      return exp && Date.now() >= exp * 1000;
    } catch {
      return true;
    }
  },

  // 3. Get User's Email
  getUsername: () => {
    const token = Cookies.get('jwtToken');
    if (!token) return null;
    try {
      const decoded = jwtDecode(token);
      return decoded.sub || decoded.email || null;
    } catch { 
      return null; 
    }
  },

  // ✅ 4. RESTORED: Get User Roles
  getUserRoles: () => {
    const token = Cookies.get('jwtToken');
    if (!token) return [];
    try {
      const decoded = jwtDecode(token);
      // Backend might send 'role', 'roles', or 'authorities'
      const roles = decoded.role || decoded.roles || decoded.authorities || [];
      
      // Always return an array (e.g. ["DONOR"])
      return Array.isArray(roles) ? roles : [roles];
    } catch (error) {
      console.error("Token decode failed:", error);
      return [];
    }
  },

  // Logout helper
  logout: () => {
    Cookies.remove('jwtToken');
  }
};



export default JwtUtils;