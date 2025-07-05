import React from 'react';
import { Link } from 'react-router-dom';
import './Header.css'; // Create this CSS file for styling

const Header: React.FC = () => {
  return (
    <header className="app-header">
      <nav>
        <ul>
          <li>
            <Link to="/">Home</Link>
          </li>
          <li>
            <Link to="/add-movie">Add Movie</Link>
          </li>
        </ul>
      </nav>
    </header>
  );
};

export default Header;
