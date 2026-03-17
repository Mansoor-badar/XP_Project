// import Navbar from "../components/Navbar";
// import UploadCard from "../components/UploadCard";
// import PasteCard from "../components/PasteCard";
// import ResultsTable from "../components/ResultsTable";

// export default function Dashboard() {
//   return (
//     <div className="dashboard">
//       <Navbar />

//       <div className="header">
//         <h1>Upload & Scan Code</h1>
//         <p>
//           Upload your source code for security and complexity analysis
//         </p>
//       </div>

//       <div className="cards">
//         <UploadCard />
//         <PasteCard />
//       </div>

//       <div className="actions">
//         <button className="run">Run Scan</button>
//         <button className="clear">Clear</button>
//       </div>

//       <ResultsTable />
//     </div>
//   );
// }

import { useState } from "react";
import Navbar from "../components/Navbar";
import UploadCard from "../components/UploadCard";
import PasteCard from "../components/PasteCard";
import ResultsTable from "../components/ResultsTable";

export default function Dashboard() {
  const [files, setFiles] = useState([]);
  const [code, setCode] = useState("");
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [resetKey, setResetKey] = useState(0); //variable to hold a key that changes and forces a brand new comp instance

  const handleRunScan = async () => {
    if (files.length === 0 && code.trim() === "") {
      alert("Please upload a file or paste code before scanning.");
      return;
    }
    setLoading(true);

    const requestData = { //prep the request payload
      fileName: files.length > 0 ? files[0].name : "pastedCode.java",
      sourceCode: files.length > 0 ? await files[0].text() : code,
    };

    try {
      const response = await fetch("http://localhost:8080/api/analysis/analyze", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(requestData),
      });

      if (!response.ok) {
        const error = await response.text();
        throw new Error(error);
      }

    const data = await response.json();
    setResults([data]); //store the result
  } catch (err) {
    console.error(err);
    alert("Error: " + err.message);
  } finally {
    setLoading(false);
  }
};

  const handleClear = () => {
    setFiles([]);
    setCode("");
    setResults([]);
    setResetKey(prev => prev + 1); //add a new key to reset component with each clear
  };

  return (
    <div className="dashboard">
      <Navbar />

      <div className="header">
        <h1>Upload & Scan Code</h1>
        <p>Upload your source code for security and complexity analysis</p>
      </div>

      <div className="cards">
        <UploadCard key={resetKey} setFiles={setFiles} />
        <PasteCard code={code} setCode={setCode} />
      </div>

      <div className="actions">
        <button className="run" onClick={handleRunScan} disabled={loading}>
          {loading ? "Scanning..." : "Run Scan"}
        </button>
        <button className="clear" onClick={handleClear}>
          Clear
        </button>
      </div>
      

      <ResultsTable results={results} />
    </div>
  );
}