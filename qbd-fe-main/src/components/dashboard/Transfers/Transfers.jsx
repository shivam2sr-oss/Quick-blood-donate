import React, { useEffect, useState } from "react";
import {
  Truck,
  ArrowDownLeft,
  ArrowUpRight,
  PackageCheck,
  Clock,
} from "lucide-react";
import TransferApi from "../../../services/TransferService";

// Helper: Convert Backend Enum to UI Text
const formatBloodGroup = (group) => {
  const map = {
    A_POS: "A+",
    A_NEG: "A-",
    B_POS: "B+",
    B_NEG: "B-",
    O_POS: "O+",
    O_NEG: "O-",
    AB_POS: "AB+",
    AB_NEG: "AB-",
  };
  return map[group] || group;
};

const Transfers = () => {
  const [transfers, setTransfers] = useState([]);
  const [view, setView] = useState("incoming"); // 'incoming' or 'outgoing'
  const [loading, setLoading] = useState(true);

  // HARDCODED ID (Matches your DB)
  // Later you will get this from the logged-in user's context/token
  const CBB_ID = 1;

  // --- FETCH DATA ---
  const fetchTransfers = async () => {
    try {
      setLoading(true);

      // 2. Use Service to fetch history
      const data = await TransferApi.fetchHistory(CBB_ID);

      setTransfers(data || []);
    } catch (error) {
      console.error("Error fetching transfers:", error);
      // Service handles the main error logging
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTransfers();
  }, []);

  // --- HANDLE RECEIVE ACTION ---
  const handleReceive = async (transferId) => {
    try {
      // 3. Use Service to receive stock
      await TransferApi.receiveTransfer(transferId);

      // Refresh list to update status and inventory
      fetchTransfers();
      alert("Stock received and Inventory updated!");
    } catch (error) {
      console.error("Failed to receive:", error);
      alert("Error receiving stock.");
    }
  };

  // --- FILTERING LOGIC ---
  // Incoming = I am the 'toOrg'
  // Outgoing = I am the 'fromOrg'
  const filteredTransfers = transfers.filter((t) => {
    if (view === "incoming") return t.toOrg.id === CBB_ID;
    return t.fromOrg.id === CBB_ID;
  });

  return (
    <div className="space-y-6 animate-fade-in">
      {/* HEADER & TABS */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 bg-white p-6 rounded-2xl border border-gray-100 shadow-sm">
        <div>
          <h2 className="text-xl font-bold text-gray-900 flex items-center gap-2">
            <Truck className="text-indigo-600" /> Logistics & Transfers
          </h2>
          <p className="text-gray-500 text-sm mt-1">
            Track blood movement between Nodes, Hospitals, and other Banks.
          </p>
        </div>

        <div className="flex bg-gray-100 p-1 rounded-lg">
          <button
            onClick={() => setView("incoming")}
            className={`flex items-center gap-2 px-4 py-2 text-sm font-medium rounded-md transition-all ${view === "incoming" ? "bg-white text-indigo-600 shadow-sm" : "text-gray-500 hover:text-gray-700"}`}
          >
            <ArrowDownLeft size={16} /> Incoming
          </button>
          <button
            onClick={() => setView("outgoing")}
            className={`flex items-center gap-2 px-4 py-2 text-sm font-medium rounded-md transition-all ${view === "outgoing" ? "bg-white text-indigo-600 shadow-sm" : "text-gray-500 hover:text-gray-700"}`}
          >
            <ArrowUpRight size={16} /> Outgoing
          </button>
        </div>
      </div>

      {/* TRANSFER LIST */}
      {loading ? (
        <div className="text-center py-10 text-gray-400">
          Loading transfers...
        </div>
      ) : filteredTransfers.length === 0 ? (
        <div className="text-center py-16 bg-white rounded-2xl border border-dashed border-gray-200">
          <div className="w-16 h-16 bg-indigo-50 text-indigo-500 rounded-full flex items-center justify-center mx-auto mb-4">
            <Truck size={32} />
          </div>
          <h3 className="text-lg font-bold text-gray-900">
            No {view} transfers found
          </h3>
          <p className="text-gray-500">History is empty.</p>
        </div>
      ) : (
        <div className="space-y-4">
          {filteredTransfers.map((t) => (
            <div
              key={t.id}
              className="bg-white p-5 rounded-xl border border-gray-100 shadow-sm flex flex-col md:flex-row items-center justify-between gap-4 transition-transform hover:scale-[1.01]"
            >
              {/* LEFT: Icon & Basic Info */}
              <div className="flex items-center gap-4 w-full md:w-auto">
                <div
                  className={`p-3 rounded-full ${view === "incoming" ? "bg-indigo-50 text-indigo-600" : "bg-orange-50 text-orange-600"}`}
                >
                  {view === "incoming" ? (
                    <ArrowDownLeft size={24} />
                  ) : (
                    <ArrowUpRight size={24} />
                  )}
                </div>
                <div>
                  <h4 className="text-sm font-bold text-gray-900">
                    {view === "incoming"
                      ? `From: ${t.fromOrg.name}`
                      : `To: ${t.toOrg.name}`}
                  </h4>
                  <p className="text-xs text-gray-500">{t.transferType}</p>
                  <span className="text-xs text-gray-400 flex items-center gap-1 mt-1">
                    <Clock size={12} />{" "}
                    {new Date(t.transferDate).toLocaleDateString()}
                  </span>
                </div>
              </div>

              {/* MIDDLE: Blood Details */}
              <div className="flex items-center gap-6">
                <div className="text-center">
                  <span className="block text-xs text-gray-400 uppercase">
                    Group
                  </span>
                  <span className="font-black text-lg text-gray-800">
                    {formatBloodGroup(t.bloodGroup)}
                  </span>
                </div>
                <div className="text-center">
                  <span className="block text-xs text-gray-400 uppercase">
                    Quantity
                  </span>
                  <span className="font-bold text-lg text-gray-800">
                    {t.quantity}{" "}
                    <span className="text-xs font-normal text-gray-500">
                      Units
                    </span>
                  </span>
                </div>
              </div>

              {/* RIGHT: Status & Action */}
              <div className="flex items-center gap-3 w-full md:w-auto justify-end">
                <span
                  className={`px-3 py-1 rounded-full text-xs font-bold border 
                            ${
                              t.status === "DELIVERED"
                                ? "bg-emerald-50 text-emerald-600 border-emerald-100"
                                : "bg-amber-50 text-amber-600 border-amber-100"
                            }`}
                >
                  {t.status}
                </span>

                {/* SHOW "RECEIVE" BUTTON ONLY IF: Incoming AND Status is DISPATCHED */}
                {view === "incoming" && t.status === "DISPATCHED" && (
                  <button
                    onClick={() => handleReceive(t.id)}
                    className="bg-indigo-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-indigo-700 shadow-md shadow-indigo-100 flex items-center gap-2"
                  >
                    <PackageCheck size={16} /> Receive Stock
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

export default Transfers;
