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

  return (
    <div className="dashboard">
      <Navbar />

      <div className="header">
        <h1>Upload & Scan Code</h1>
        <p>Upload your source code for security and complexity analysis</p>
      </div>

      <div className="cards">
        <UploadCard setFiles={setFiles} />
        <PasteCard code={code} setCode={setCode} />
      </div>

      <div className="actions">
        <button className="run">Run Scan</button>
        <button className="clear">Clear</button>
      </div>

      <ResultsTable results={results} />
    </div>
  );
}