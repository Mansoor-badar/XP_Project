import Header from "./Header.jsx";
import Footer from "./Footer.jsx";
function Layout(props) {
  return (
      <div className="layout">
        <Header />
        <main>{props.children}</main>
        <Footer />
      </div>
  );
}

export default Layout;
