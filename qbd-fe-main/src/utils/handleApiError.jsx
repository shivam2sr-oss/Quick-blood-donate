export const handleApiError = (error, moduleName) => {
  if (error.response) {
    // The request was made and the server responded with a status code
    console.error(
      `[${moduleName}] Error ${error.response.status}:`,
      error.response.data,
    );
    throw error.response.data; // Throw data so UI can show specific message
  } else if (error.request) {
    // The request was made but no response was received
    console.error(`[${moduleName}] No response received:`, error.request);
    throw new Error("Server is unreachable. Please try again.");
  } else {
    // Something happened in setting up the request
    console.error(`[${moduleName}] Request failed:`, error.message);
    throw error;
  }
};
