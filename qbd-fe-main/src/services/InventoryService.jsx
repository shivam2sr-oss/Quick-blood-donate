import apiInterceptor from "../config/ApiInterceptor"; 
import { handleApiError } from "../utils/handleApiError";

const InventoryApi ={
    fetchInventory : async (CBB_ID) => {
    try {
      const response = await apiInterceptor.get(`/inventory/${CBB_ID}`);
      
      return response.data;
    } catch (error) {
        console.log("error in ",error);
        
    }
  },
  addStock: async (cbbId, bloodGroup, quantity) => {
    try {
      const response = await apiInterceptor.post(`/inventory/add`, null, {
        params: { cbbId, bloodGroup, quantity }
      });
      return response.data;
    } catch (error) {
      handleApiError(error, "Inventory:Add");
    }
  },

  // POST /inventory/deduct
  deductStock: async (cbbId, bloodGroup, quantity) => {
    try {
      const response = await apiInterceptor.post(`/inventory/deduct`, null, {
        params: { cbbId, bloodGroup, quantity }
      });
      return response.data;
    } catch (error) {
      handleApiError(error, "Inventory:Deduct");
    }
  }

}
export default InventoryApi;