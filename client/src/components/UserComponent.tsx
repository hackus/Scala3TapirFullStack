import React from 'react';
import UserService from '../services/UserService';
import Loader from './Loader';

const UserComponent: React.FC<{}> = () => {
  const service = UserService();

  return (
    <>
      <div className="user-modal-container">
        <div className="user-modal-background"/>

        {service.status === 'loading' && <Loader />}

        {service.status === 'loaded' && (
          <div className="user">
            <h2>{service.payload.name}</h2>
          </div>
        )}

        {service.status === 'error' && (
          <div className="user">
            Error, something weird happened with the user.
          </div>
        )}
      </div>
    </>
  );
};


export default UserComponent;