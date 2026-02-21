import apiInterceptor from "../config/ApiInterceptor";
import Cookies from 'js-cookie';
// Use the shared utility you created earlier
import { handleApiError } from "../utils/handleApiError";

const AuthApi = {
  /**
   * 🔐 Login API
   */
  login: async (loginDTO) => {
    console.log(loginDTO);
    
    try {
      const response = await apiInterceptor.post("/auth/login", loginDTO);
      
      console.info("✅ Login successful:", response.data);

      // --- FIX 1: SAVE THE TOKEN ---
      // The interceptor reads 'jwtToken' from cookies. We must save it here.
      if (response.data.token) {
        Cookies.set('jwtToken', response.data.token, { expires: 1 }); // Expires in 1 day
      }

      return response.data;
    } catch (error) {
      handleApiError(error, "login");
    }
  },

  /**
   * 📝 Signup API (Generic for Donors, Staff, Nodes)
   */
  signup: async (signupData) => {
    try {
      // --- FIX 2: HANDLE DYNAMIC DATA ---
      // Don't hardcode "PLAYER". Pass whatever the form sends (role, bloodGroup, etc.)
      console.info("📤 Sending signup data:", signupData);
      
      const response = await apiInterceptor.post("/auth/signup", signupData);
      
      console.info("✅ Signup successful:", response.data);
      return response.data;
    } catch (error) {
      handleApiError(error, "signup");
    }
  },

  /**
   * 🚪 Logout Helper
   */
  logout: () => {
    Cookies.remove('jwtToken');
    window.location.href = '/login';
  },
  fetchCbb: async () => {
    try {
      const response = await apiInterceptor.get('/auth/cbb');
      console.info("✅ Fetched CBB list:", response.data);
      return response.data;
    } catch (error) {
      handleApiError(error, "fetchCbb");
    }
  }
};

export default AuthApi;