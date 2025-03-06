import { useEffect, useState } from 'react';
import { Service } from './Service';
import User from "../types/User"


const UserService = () => {
  const [result, setResult] = useState<Service<User>>({
    status: 'loading'
  });

  useEffect(() => {
    fetch('/hello?name=FirstUser')
            .then(async response => {
                const data = await response.json();

                // check for error response
                if (!response.ok) {
                    // get error message from body or default to response statusText
                    const error = (data && data.message) || response.statusText;
                    return Promise.reject(error);
                }

                setResult({ status: 'loaded', payload: data })
            })
            .catch(error => {
                setResult({ status: 'error', error })
                console.error('There was an error!', error);
            });
    }

//     fetch("/api/hello?name=FirstUser")
//       .then(async response => {
//           const data = await response.json();
//       })
// //       .then(response => response.json())
//       .then(async response => {
//           await response;
//           setResult({ status: 'loaded', payload: response })
//         })
//       .catch(error => setResult({ status: 'error', error }));
//   }
  , []);





  return result;
};

export default UserService;