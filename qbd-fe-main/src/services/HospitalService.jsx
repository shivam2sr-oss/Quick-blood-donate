import apiInterceptor from "../config/ApiInterceptor";
import { handleApiError } from "../utils/handleApiError";

const HospitalApi = {
  /**
   * 🔐 Login API
   */
  getRequests: async (hospitalId) => {
    try {
      const response = await apiInterceptor.get(
        `hospitals/requests/${hospitalId}`,
      );

      console.log("✅ fetch successfully:", response.data);

      return response.data;
    } catch (error) {
      handleApiError(error, "fetch request");
    }
  },
  submitRequest: async(hospitalRequestCreateDTO) =>{
     try {
        console.log(hospitalRequestCreateDTO);
        
      const response = await apiInterceptor.post('/hospital-requests',hospitalRequestCreateDTO);
        console.log(response.data);
        
      console.log("✅ submit successfully:", response.data);

      return response.data;
    } catch (error) {
        console.log(error);
        
      handleApiError(error, "submit request");
    }
  }
};
export default HospitalApi;
