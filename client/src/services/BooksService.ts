import { useEffect, useState } from 'react';
import { Service } from './Service';
import Book from '../types/Book';

export interface Books {
  results: Book[];
}

const BooksService = (url: string) => {
  const [result, setResult] = useState<Service<Book[]>>({
    status: 'loading'
  });

  useEffect(() => {
    if (url) {
      setResult({ status: 'loading' });
      fetch(url)
        .then(response => response.json())
        .then(response => setResult({ status: 'loaded', payload: response }))
        .catch(error => setResult({ status: 'error', error }));
    }
  }, [url]);

  return result;
};

export default BooksService;