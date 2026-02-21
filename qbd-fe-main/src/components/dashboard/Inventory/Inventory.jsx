import React, { useEffect, useState } from "react";
// 1. Import the Service
import InventoryService from "../../../services/InventoryService"; 
import { Droplet, AlertTriangle, Clock, RefreshCw } from "lucide-react";

// Helper: Convert Backend Enum (A_POS) to UI Text (A+)
const formatBloodGroup = (group) => {
    const map = {
        'A_POS': 'A+', 'A_NEG': 'A-',
        'B_POS': 'B+', 'B_NEG': 'B-',
        'O_POS': 'O+', 'O_NEG': 'O-',
        'AB_POS': 'AB+', 'AB_NEG': 'AB-'
    };
    return map[group] || group;
};

const Inventory = ({cbbId}) => {
  
  const [inventory, setInventory] = useState([]);
  const [loading, setLoading] = useState(true);
    console.log(cbbId);
    
  // HARDCODED CBB ID (Make sure this ID exists in your DB)
  // const cbbId = 1; 

  const fetchInventory = async () => {
    try {
      setLoading(true);
      // 2. Use Service
      const data = await InventoryService.fetchInventory(cbbId);
      console.log(data);
      
      setInventory(data || []);
    } catch (err) {
      console.error("Error fetching inventory:", err);
      // Service handles the specific error logging
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchInventory();
  }, []);

  // Stats Calculation
  const totalUnits = inventory.reduce((sum, item) => sum + item.quantity, 0);
  const lowStockCount = inventory.filter(item => item.quantity < 10).length;

  const allGroups = [
      'A_POS', 'A_NEG', 'B_POS', 'B_NEG', 
      'O_POS', 'O_NEG', 'AB_POS', 'AB_NEG'
  ];


  if (loading) return <div className="p-10 text-center text-gray-500">Loading live inventory...</div>;

  return (
    <div className="space-y-8 animate-fade-in">
      {/* STATS ROW */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="p-6 rounded-2xl border bg-rose-50 border-rose-100 shadow-sm flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-500 mb-1">Total Units</p>
              <h3 className="text-3xl font-bold text-gray-900">{totalUnits}</h3>
              <span className="text-xs font-semibold text-rose-600 bg-white/60 px-2 py-0.5 rounded border border-black/5 mt-2 inline-block">
                  Live Data
              </span>
            </div>
            <div className="p-3 bg-white rounded-xl shadow-sm">
               <Droplet className="text-rose-600" size={24} />
            </div>
        </div>

        <div className="p-6 rounded-2xl border bg-amber-50 border-amber-100 shadow-sm flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-500 mb-1">Low Stock Alerts</p>
              <h3 className="text-3xl font-bold text-gray-900">{lowStockCount}</h3>
              <span className="text-xs font-semibold text-amber-700 bg-white/60 px-2 py-0.5 rounded border border-black/5 mt-2 inline-block">
                  Action Needed
              </span>
            </div>
            <div className="p-3 bg-white rounded-xl shadow-sm">
               <AlertTriangle className="text-amber-600" size={24} />
            </div>
        </div>

        <div className="p-6 rounded-2xl border bg-white border-gray-100 shadow-sm flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-500 mb-1">System Status</p>
              <h3 className="text-xl font-bold text-gray-900">Online</h3>
              <button onClick={fetchInventory} className="flex items-center gap-1 text-xs font-semibold text-gray-500 hover:text-rose-600 mt-2">
                 <RefreshCw size={12} /> Refresh Data
              </button>
            </div>
            <div className="p-3 bg-gray-50 rounded-xl shadow-sm">
               <Clock className="text-gray-400" size={24} />
            </div>
        </div>
      </div>

      {/* BLOOD GRID */}
      <div className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm">
        <div className="flex items-center justify-between mb-6">
            <h3 className="text-lg font-bold text-gray-800">Current Stock Levels</h3>
        </div>
        
        <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-8 gap-4">
            {allGroups.map((groupEnum) => {
                const data = inventory.find(i => i.bloodGroup === groupEnum) || { quantity: 0 };
                const formattedName = formatBloodGroup(groupEnum);
                const isLow = data.quantity < 10;
                const fillPercent = Math.min((data.quantity / 100) * 100, 100);

                return (
                    <div key={groupEnum} className={`flex flex-col items-center p-4 rounded-xl border transition-colors group 
                        ${isLow ? 'bg-red-50 border-red-100' : 'bg-gray-50 border-gray-100 hover:border-rose-200 hover:bg-rose-50'}`}>
                        
                        <span className={`text-2xl font-black mb-2 ${isLow ? 'text-red-400' : 'text-gray-300 group-hover:text-rose-500'}`}>
                            {formattedName}
                        </span>
                        
                        <span className={`text-sm font-bold ${isLow ? 'text-red-700' : 'text-gray-900'}`}>
                            {data.quantity} Units
                        </span>
                        
                        <div className="w-full bg-gray-200 h-1.5 rounded-full mt-3 overflow-hidden">
                            <div 
                                className={`h-full rounded-full transition-all duration-1000 ${isLow ? 'bg-red-500' : 'bg-rose-500'}`} 
                                style={{ width: `${fillPercent}%` }}>
                            </div>
                        </div>
                    </div>
                );
            })}
        </div>
      </div>
    </div>
  );
};

export default Inventory;