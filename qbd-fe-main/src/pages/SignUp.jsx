import React, { useEffect, useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import AuthService from "../services/AuthService";
import {
  Heart,
  Building2,
  MapPin,
  Activity,
  ArrowLeft,
  CheckCircle2,
  Loader2,
  AlertTriangle,
} from "lucide-react";
import { toast } from "react-toastify";
import { use } from "i18next";

const SignUp = () => {
  const navigate = useNavigate();
  const [step, setStep] = useState(1); // 1=Role, 2=Info, 3=Medical
  const [loading, setLoading] = useState(false);
  const [cbb, setCbb] = useState([]);
  // ✅ NEW: Added medicalHistory array to state
  const [formData, setFormData] = useState({
    fullName: "",
    email: "",
    password: "",
    role: "",
    contactNumber: "",
    address: "",
    bloodGroup: "A_POS",
    dob: "",
    gender: "MALE",
    weight: "",
    district: "",
    parentOrganizationId: null,
    medicalHistory: [],
  });

  // --- MEDICAL CHECKLIST ---
  const diseases = [
    "HIV / AIDS",
    "Hepatitis B / C",
    "Diabetes (Insulin)",
    "Tuberculosis",
    "Cancer",
    "Heart Disease",
    "Malaria (Last 3 months)",
    "Taking Antibiotics",
  ];

  useEffect(() => {
  fetchCbb();
}, []);

const fetchCbb = async () => {
  try {
    const response = await AuthService.fetchCbb(); // ✅ await
    setCbb(response || []);                  // ✅ always array
  } catch (error) {
    console.error("Error fetching CBB:", error);
    setCbb([]);                                   // safety
  }
};


  // Logic to handle checking/unchecking boxes
  const handleCheckboxChange = (disease) => {
    setFormData((prev) => {
      const history = prev.medicalHistory.includes(disease)
        ? prev.medicalHistory.filter((item) => item !== disease) // Remove
        : [...prev.medicalHistory, disease]; // Add
      return { ...prev, medicalHistory: history };
    });
  };

  // Logic to decide whether to Submit or go to Step 3
  const handleNext = (e) => {
    e.preventDefault();

    if (!validateStep2()) return;

    if (step === 2 && formData.role === "DONOR") {
      setStep(3); // go to medical step
    } else {
      handleSubmit(e); // staff submit directly
    }
  };

  const handleSubmit = async (e) => {
    if (e) e.preventDefault();
    setLoading(true);
    try {
      console.log("Sending Data:", formData);
      await AuthService.signup(formData);
      toast.success("Registration Successful! Please Login.");
      // alert('Registration Successful! Please Login.');
      navigate("/login");
    } catch (err) {
      console.error(err);

      toast.error("Registration Failed: " + (err.message || "Unknown error"));
    } finally {
      setLoading(false);
    }
  };

  const roles = [
    {
      id: "DONOR",
      label: "Blood Donor",
      icon: <Heart size={32} />,
      color: "bg-rose-50 text-rose-600 border-rose-200",
    },
    {
      id: "NODE_STAFF",
      label: "Collection Node",
      icon: <MapPin size={32} />,
      color: "bg-blue-50 text-blue-600 border-blue-200",
    },
    {
      id: "CBB_STAFF",
      label: "Central Blood Bank",
      icon: <Building2 size={32} />,
      color: "bg-indigo-50 text-indigo-600 border-indigo-200",
    },
    {
      id: "HOSPITAL_STAFF",
      label: "Hospital",
      icon: <Activity size={32} />,
      color: "bg-emerald-50 text-emerald-600 border-emerald-200",
    },
  ];

  const handleRoleSelect = (id) => {
    setFormData({ ...formData, role: id });
    setStep(2);
  };
  const handleChange = (e) =>
    setFormData({ ...formData, [e.target.name]: e.target.value });

  // Helper for progress bar
  const StepIndicator = ({ num, text, active }) => (
    <div
      className={`flex items-center gap-3 transition-opacity ${active ? "opacity-100" : "opacity-40"}`}
    >
      <div
        className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold ${active ? "bg-rose-600" : "bg-slate-700"}`}
      >
        {num}
      </div>
      <span className="font-medium">{text}</span>
    </div>
  );

  const validateStep2 = () => {
    if (formData.fullName.trim().length < 3) {
      toast.error("Full name must be at least 3 characters");
      return false;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(formData.email)) {
      toast.error("Enter a valid email address");
      return false;
    }

    if (formData.password.length < 8) {
      toast.error("Password must be at least 8 characters");
      return false;
    }

    const phoneRegex = /^[0-9]{10}$/;
    if (!phoneRegex.test(formData.contactNumber)) {
      toast.error("Contact number must be 10 digits");
      return false;
    }

    if (!formData.address.trim()) {
      toast.error("Address is required");
      return false;
    }

    // Staff must provide district
    if (formData.role !== "DONOR" && !formData.district.trim()) {
      toast.error("District is required");
      return false;
    }

    // Donor specific checks
    if (formData.role === "DONOR") {
      if (!formData.weight || Number(formData.weight) < 40) {
        toast.error("Weight must be at least 40 kg to donate");
        return false;
      }

      if (!formData.dob) {
        toast.error("Date of birth is required");
        return false;
      }

      const age =
        new Date().getFullYear() - new Date(formData.dob).getFullYear();

      if (age < 18) {
        toast.error("You must be at least 18 years old to donate");
        return false;
      }
    }

    return true;
  };

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
      <div className="bg-white w-full max-w-3xl rounded-3xl shadow-xl overflow-hidden flex flex-col md:flex-row">
        {/* LEFT PANEL */}
        <div className="bg-slate-900 text-white p-8 md:w-1/3 flex flex-col justify-between">
          <div>
            <h2 className="text-3xl font-bold mb-8">Join BloodFlow</h2>
            <div className="space-y-4">
              <StepIndicator num={1} text="Role" active={step >= 1} />
              <StepIndicator num={2} text="Details" active={step >= 2} />
              {/* Only show step 3 indicator for donors */}
              {formData.role === "DONOR" && (
                <StepIndicator num={3} text="Medical" active={step === 3} />
              )}
            </div>
          </div>
          <div className="mt-8">
            <Link
              to="/login"
              className="text-slate-400 text-sm hover:text-white"
            >
              Already registered? Login
            </Link>
          </div>
        </div>

        {/* RIGHT PANEL */}
        <div className="p-8 md:w-2/3 overflow-y-auto max-h-[90vh]">
          {/* STEP 1: ROLE */}
          {step === 1 && (
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              {roles.map((r) => (
                <button
                  key={r.id}
                  onClick={() => handleRoleSelect(r.id)}
                  className={`p-5 rounded-xl border-2 text-left transition-all ${r.color} hover:shadow-md`}
                >
                  <div className="mb-3">{r.icon}</div>
                  <div className="font-bold">{r.label}</div>
                </button>
              ))}
            </div>
          )}

          {/* STEP 2: BASIC INFO */}
          {step === 2 && (
            <form onSubmit={handleNext} className="space-y-4">
              <button
                type="button"
                onClick={() => setStep(1)}
                className="text-sm text-gray-500 mb-2"
              >
                ← Back
              </button>
              <h3 className="text-xl font-bold">Details</h3>

              {(formData.role === "NODE_STAFF" ||
                formData.role === "HOSPITAL_STAFF") && (
                <select
                  name="parentOrganizationId"
                  required
                  value={formData.parentOrganizationId || ""}
                  onChange={handleChange}
                  className="p-3 border rounded w-full bg-white"
                >
                  <option value="">Select Central Blood Bank</option>
                  {cbb.map((org) => (
                    <option key={org.id} value={org.id}>
                      {org.name} ({org.city})
                    </option>
                  ))}
                </select>
              )}

              <input
                type="text"
                name="fullName"
                required
                minLength={3}
                placeholder="Full Name"
                onChange={handleChange}
                className="p-3 border rounded w-full"
              />
              <input
                type="email"
                name="email"
                required
                placeholder="Email"
                onChange={handleChange}
                className="p-3 border rounded w-full"
              />
              <input
                type="password"
                name="password"
                required
                minLength={8}
                placeholder="Password"
                onChange={handleChange}
                className="p-3 border rounded w-full"
              />
              <input
                type="text"
                name="address"
                required
                placeholder={formData.role === "DONOR" ? "City" : "City "}
                onChange={handleChange}
                className="p-3 border rounded w-full"
              />
              <input
                type="tel"
                name="contactNumber"
                required
                pattern="[0-9]{10}"
                placeholder="Phone"
                onChange={handleChange}
                className="p-3 border rounded w-full"
              />

              {formData.role === "DONOR" && (
                <div className="grid grid-cols-2 gap-4">
                  <input
                    type="number"
                    name="weight"
                    min="40"
                    max="110"
                    placeholder="Weight (kg)"
                    required
                    onChange={handleChange}
                    className="p-3 border rounded"
                  />
                  <select
                    name="bloodGroup"
                    onChange={handleChange}
                    className="p-3 border rounded bg-white"
                  >
                    <option value="A_POS">A+</option>
                    <option value="A_NEG">A-</option>
                    <option value="B_POS">B+</option>
                    <option value="B_NEG">B-</option>
                    <option value="O_POS">O+</option>
                    <option value="O_NEG">O-</option>
                    <option value="AB_POS">AB+</option>
                    <option value="AB_NEG">AB-</option>
                  </select>
                </div>
              )}
              {formData.role === "DONOR" && (
                <input
                  type="date"
                  name="dob"
                  required
                  onChange={handleChange}
                  className="p-3 border rounded w-full text-gray-500"
                />
              )}

              {(formData.role === "NODE_STAFF" ||
                formData.role === "HOSPITAL_STAFF" ||
                formData.role === "CBB_STAFF") && (
                <input
                  type="text"
                  name="district"
                  required
                  placeholder="district"
                  onChange={handleChange}
                  className="p-3 border rounded w-full"
                />
              )}

              <button
                type="submit"
                className="w-full bg-slate-900 text-white font-bold py-3 rounded-xl mt-4"
              >
                {formData.role === "DONOR" ? "Next: Medical Check" : "Register"}
              </button>
            </form>
          )}

          {/* ✅ STEP 3: MEDICAL HISTORY (Donors Only) */}
          {step === 3 && (
            <div className="space-y-6 animate-fade-in">
              <button
                type="button"
                onClick={() => setStep(2)}
                className="text-sm text-gray-500"
              >
                ← Back
              </button>

              <div className="bg-rose-50 border border-rose-100 p-4 rounded-xl flex gap-3">
                <AlertTriangle className="text-rose-600 shrink-0" />
                <p className="text-sm text-rose-800">
                  Please check any conditions that apply to you. This is crucial
                  for safety.
                </p>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                {diseases.map((d) => (
                  <label
                    key={d}
                    className={`flex items-center gap-3 p-3 rounded-lg border cursor-pointer transition-all ${formData.medicalHistory.includes(d) ? "bg-rose-100 border-rose-500" : "bg-white hover:border-rose-300"}`}
                  >
                    <input
                      type="checkbox"
                      checked={formData.medicalHistory.includes(d)}
                      onChange={() => handleCheckboxChange(d)}
                      className="w-5 h-5 text-rose-600 rounded focus:ring-rose-500"
                    />
                    <span className="text-gray-700 font-medium">{d}</span>
                  </label>
                ))}
              </div>

              <button
                onClick={() => handleSubmit()}
                disabled={loading}
                className="w-full bg-rose-600 text-white font-bold py-3 rounded-xl hover:bg-rose-700 flex justify-center gap-2"
              >
                {loading ? (
                  <Loader2 className="animate-spin" />
                ) : (
                  "Confirm & Register"
                )}
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default SignUp;
