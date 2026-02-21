import apiInterceptor from "../config/ApiInterceptor"; 
import { handleApiError } from "../utils/handleApiError";

const TransferApi = {
  // GET /blood-transfer/history/{orgId}
  fetchHistory: async (orgId) => {
    try {
      const response = await apiInterceptor.get(
        `/blood-transfer/history/${orgId}`,
      );
      return response.data;
    } catch (error) {
      handleApiError(error, "Transfers:History");
    }
  },

  // POST /blood-transfer/dispatch
  dispatchTransfer: async (transferData) => {
    // transferData should be object: { fromOrgId, toOrgId, bloodGroup, quantity, transferType }
    try {
      // Since your controller uses @RequestParam, we pass as params
      const response = await apiInterceptor.post(
        `/blood-transfer/dispatch`,
        null,
        {
          params: transferData,
        },
      );
      return response.data;
    } catch (error) {
      handleApiError(error, "Transfers:Dispatch");
    }
  },

  // POST /blood-transfer/receive/{transferId}
  receiveTransfer: async (transferId) => {
    try {
      const response = await apiInterceptor.post(
        `/blood-transfer/receive/${transferId}`,
      );
      return response.data;
    } catch (error) {
      handleApiError(error, "Transfers:Receive");
    }
  },
};

export default TransferApi;
