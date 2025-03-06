import React from 'react';
import BooksService from '../services/BooksService';
import Loader from './Loader';
import Book from '../types/Book';
import Books from '../services/BooksService';

export interface Props {
  url: string;
}

const BooksComponent: React.FC<Props> = ({url}) => {
  const service = BooksService(url);

  return (
    <>
        {service!=null &&  service.status === "loading" && (<Loader />)}

        { (service.status === "loaded") &&
          (
            <div className="book-modal-container">
              <table>
                <thead>
                  <tr>
                    <th>Title</th>
                    <th>Age</th>
                    <th>Author name</th>
                  </tr>
                </thead>
                <tbody>
                  { service.payload.map((book: Book, index) => {
                      return <tr key={index}>
                        <td>{book.title}</td>
                        <td>{book.year}</td>
                        <td>{book.author.name}</td>
                      </tr>;
                  })}
                </tbody>
              </table>
            </div>
          )
        }

        {service.status === 'error' && (
          <div className="user">
            Error, something weird happened with the book service.
          </div>
        )}
    </>
  );
};

export default BooksComponent;