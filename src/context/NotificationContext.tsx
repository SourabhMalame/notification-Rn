// NotificationContext.tsx
import React, { createContext, useContext, useState } from 'react';

type RemoteMessage = {
  messageId?: string;
  notification?: {
    title?: string;
    body?: string;
  };
  data?: any;
};

type NotificationContextType = {
  notifications: RemoteMessage[];
  addNotification: (message: RemoteMessage) => void;
};

const NotificationContext = createContext<NotificationContextType>({
  notifications: [],
  addNotification: () => {},
});

export const NotificationProvider: React.FC<{children: React.ReactNode}> = ({ children }) => {
  const [notifications, setNotifications] = useState<RemoteMessage[]>([]);

  const addNotification = (message: RemoteMessage) => {
    setNotifications(prev => [...prev, message]);
  };

  return (
    <NotificationContext.Provider value={{ notifications, addNotification }}>
      {children}
    </NotificationContext.Provider>
  );
};

export const useNotifications = () => useContext(NotificationContext);