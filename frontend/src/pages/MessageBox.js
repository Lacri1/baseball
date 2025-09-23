import React from 'react';

const MessageBox = ({ message }) => (
  <div style={{
    border: '1px solid #ccc',
    padding: 10,
    marginBottom: 10,
    minHeight: 30,
    background: '#f9f9f9'
  }}>
    {message || "게임 메시지가 표시됩니다."}
  </div>
);

export default MessageBox;
