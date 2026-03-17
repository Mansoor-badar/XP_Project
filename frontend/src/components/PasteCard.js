// export default function PasteCard() {
//   return (
//     <div className="card">
//       <h3>Paste Code</h3>
//       <p>Directly paste your code</p>

//       <textarea
//         className="pastebox"
//         placeholder="Paste your code here..."
//       />
//     </div>
//   );
// }

export default function PasteCard({ code, setCode }) {
  return (
    <div className="card">
      <h3>Paste Code</h3>
      <p>Directly paste your code</p>

      <textarea
        className="pastebox"
        value={code}
        onChange={(e) => setCode(e.target.value)}
      />
    </div>
  );
}