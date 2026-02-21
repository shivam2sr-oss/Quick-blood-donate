import apiInterceptor from "../config/ApiInterceptor"; 
import { handleApiError } from "../utils/handleApiError";


const AlertApi = {
  // GET /alerts/unresolved
  fetchUnresolvedAlerts: async () => {
    try {
      const response = await apiInterceptor.get(`/alerts/unresolved`);
      console.log(response);
      
      return response.data;
    } catch (error) {
      handleApiError(error, "Alerts:FetchUnresolved");
    }
  },

  // GET /alerts
  fetchAllAlerts: async () => {
    try {
      const response = await apiInterceptor.get(`/alerts`);
      return response.data;
    } catch (error) {
      handleApiError(error, "Alerts:FetchAll");
    }
  },

  // POST /alerts/resolve/{alertId}
  resolveAlert: async (alertId) => {
    try {
      const response = await apiInterceptor.post(`/alerts/resolve/${alertId}`);
      return response.data;
    } catch (error) {
      handleApiError(error, "Alerts:Resolve");
    }
  },
};

export default AlertApi;
