import React, { useEffect, useState } from "react";
import { AlertTriangle, CheckCircle, MapPin, Clock, Filter, Activity } from "lucide-react";
import AlertApi from "../../../services/AlertService";

const Alerts = () => {const [alerts, setAlerts] = useState([]);
  const [filter, setFilter] = useState("unresolved"); // 'unresolved' or 'all'
  const [loading, setLoading] = useState(true);

  // --- FETCH ALERTS ---
  const fetchAlerts = async () => {
    try {
      setLoading(true);
      
      // 2. Use Service instead of direct API call
      let data;
      if (filter === "unresolved") {
        data = await AlertApi.fetchUnresolvedAlerts();
      } else {
        data = await AlertApi.fetchAllAlerts();
      }
      
      // Note: Service already returns response.data, so we just use 'data'
      setAlerts(data || []); 
    } catch (error) {
      console.error("Error fetching alerts:", error);
      // Optional: Add UI error state here if needed
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAlerts();
  }, [filter]); // Re-fetch when filter changes

  // --- RESOLVE ACTION ---
  const handleResolve = async (alertId) => {
    try {
      // 3. Use Service for resolving
      await AlertApi.resolveAlert(alertId);

      // Remove the resolved item from the list instantly (Optimistic UI)
      setAlerts(prev => prev.filter(a => a.id !== alertId));
    } catch (error) {
      console.error("Failed to resolve alert:", error);
      alert("Failed to mark as resolved. Check console.");
    }
  };

  // Helper to format date nicely
  const formatDate = (dateString) => {
    if(!dateString) return "Just now";
    return new Date(dateString).toLocaleString();
  };

  return (
    <div className="space-y-6 animate-fade-in">
      
      {/* HEADER & FILTERS */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 bg-white p-6 rounded-2xl border border-gray-100 shadow-sm">
        <div>
          <h2 className="text-xl font-bold text-gray-900 flex items-center gap-2">
            <Activity className="text-rose-600" /> System Alerts
          </h2>
          <p className="text-gray-500 text-sm mt-1">
            {filter === "unresolved" 
              ? "Showing active critical warnings requiring attention." 
              : "Showing complete alert history."}
          </p>
        </div>

        <div className="flex bg-gray-100 p-1 rounded-lg">
          <button 
            onClick={() => setFilter("unresolved")}
            className={`px-4 py-2 text-sm font-medium rounded-md transition-all ${filter === "unresolved" ? "bg-white text-rose-600 shadow-sm" : "text-gray-500 hover:text-gray-700"}`}
          >
            Active Issues
          </button>
          <button 
            onClick={() => setFilter("all")}
            className={`px-4 py-2 text-sm font-medium rounded-md transition-all ${filter === "all" ? "bg-white text-gray-900 shadow-sm" : "text-gray-500 hover:text-gray-700"}`}
          >
            History
          </button>
        </div>
      </div>

      {/* ALERTS LIST */}
      {loading ? (
        <div className="text-center py-10 text-gray-400">Loading alerts...</div>
      ) : alerts.length === 0 ? (
        // EMPTY STATE
        <div className="text-center py-16 bg-white rounded-2xl border border-dashed border-gray-200">
          <div className="w-16 h-16 bg-emerald-50 text-emerald-500 rounded-full flex items-center justify-center mx-auto mb-4">
            <CheckCircle size={32} />
          </div>
          <h3 className="text-lg font-bold text-gray-900">All Systems Normal</h3>
          <p className="text-gray-500">No active alerts found for this filter.</p>
        </div>
      ) : (
        // ALERT CARDS GRID
        <div className="grid gap-4">
          {alerts.map((alert) => (
            <div 
              key={alert.id} 
              className={`relative p-6 rounded-xl border-l-4 shadow-sm bg-white transition-all hover:shadow-md
                ${alert.resolved 
                  ? "border-l-gray-300 border-gray-100 opacity-75" 
                  : "border-l-rose-500 border-rose-100"
                }`}
            >
              <div className="flex justify-between items-start">
                
                {/* LEFT: CONTENT */}
                <div className="space-y-2">
                  <div className="flex items-center gap-3">
                    <span className={`px-2.5 py-0.5 rounded text-xs font-bold uppercase tracking-wide border
                      ${alert.resolved 
                        ? "bg-gray-100 text-gray-600 border-gray-200" 
                        : "bg-rose-50 text-rose-600 border-rose-100"
                      }`}>
                      {alert.resolved ? "Resolved" : "Critical"}
                    </span>
                    <span className="text-xs text-gray-400 flex items-center gap-1">
                      <Clock size={12} /> {formatDate(alert.createdAt)}
                    </span>
                  </div>

                  <h3 className="text-lg font-bold text-gray-800">
                    Low Stock Warning: <span className="text-rose-600">{alert.bloodGroup?.replace('_', ' ')}</span>
                  </h3>
                  
                  <p className="text-gray-600">{alert.message}</p>
                  
                  <div className="flex items-center gap-2 text-sm text-gray-500 mt-2">
                    <MapPin size={14} />
                    <span>Target District: <strong>{alert.targetDistrict}</strong></span>
                  </div>
                </div>

                {/* RIGHT: ACTION BUTTON */}
                {!alert.resolved && (
                  <button 
                    onClick={() => handleResolve(alert.id)}
                    className="flex items-center gap-2 px-4 py-2 bg-rose-600 text-white text-sm font-medium rounded-lg hover:bg-rose-700 active:scale-95 transition-all shadow-md shadow-rose-200"
                  >
                    <CheckCircle size={16} /> Mark Resolved
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Alerts;