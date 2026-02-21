import apiInterceptor from "../config/ApiInterceptor";

const OrganizationApi ={
    fetchOrganizationBasedOnCity: async(city) =>{
        console.log(city);
        
        try {
            const response = await apiInterceptor.get(`/organizations/nodes?city=${city}`);
            console.log(response);
            return response.data;
            
        } catch (error) {
            
        }
    }
}
export default OrganizationApi;