// export default function ResultsTable() {
//   return (
//     <div className="results">
//       <h2>Analysis Results</h2>

//       <input className="search" placeholder="Search modules..." />

//       <table>
//         <thead>
//           <tr>
//             <th>Module Name</th>
//             <th>TDI</th>
//             <th>Complexity</th>
//             <th>Vuln. Density</th>
//             <th>Risk Level</th>
//           </tr>
//         </thead>

//         <tbody>
//           <tr>
//             <td colSpan="5" className="empty">
//               No Files Scanned
//             </td>
//           </tr>
//         </tbody>
//       </table>
//     </div>
//   );
// }

export default function ResultsTable({ results }) {
  return (
    <div className="results">
      <h2>Analysis Results</h2>

      <table>
        <thead>
          <tr>
            <th>Module</th>
            <th>TDI</th>
            <th>Complexity</th>
            <th>Vulnerability</th>
            <th>Risk</th>
          </tr>
        </thead>

        <tbody>
          {results.length === 0 ? (
            <tr>
              <td colSpan="5" className="empty">
                No Files Scanned
              </td>
            </tr>
          ) : (
            results.map((r, i) => (
              <tr key={i}>
                <td>{r.module}</td>
                <td>{r.tdi}</td>
                <td>{r.complexity}</td>
                <td>{r.vulnerability}</td>
                <td>{r.risk}</td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}