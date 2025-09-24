import React from 'react';

const MessageBox = ({ message }) => (
  <div className="message-box">
    {message || "게임 메시지가 표시됩니다."}
  </div>
);

export default MessageBox;
