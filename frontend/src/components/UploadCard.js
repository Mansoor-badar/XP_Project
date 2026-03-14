// export default function UploadCard() {
//   return (
//     <div className="card">
//       <h3>Upload File</h3>
//       <p>Upload source code files</p>

//       <div className="dropzone">
//         Drag & Drop your files here
//         <br />
//         <button className="browse">Browse Files</button>
//       </div>
//     </div>
//   );
// }

export default function UploadCard({ setFiles }) {
  const handleFileChange = (e) => {
    setFiles([...e.target.files]);
  };

  return (
    <div className="card">
      <h3>Upload File</h3>
      <p>Upload source code files</p>

      <input type="file" multiple onChange={handleFileChange} />
    </div>
  );
}