// import { useState, useEffect } from 'react'
import React from 'react';
import logo from './logo.svg';
import './App.css';
import UserComponent from './components/UserComponent';
import BooksComponent from './components/BooksComponent';

function App() {
  return (
    <div className="App">
      <div>
        <UserComponent />
      </div>
       <BooksComponent url='/books/list/all' />
    </div>
  )
}

export default App;
