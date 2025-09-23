// GameMessageBox.js
import React from "react";

const GameMessageBox = ({ message }) => {
  return (
    <div style={{
      position: "fixed",
      left: 20,
      bottom: 20,
      padding: "10px 16px",
      backgroundColor: "rgba(0,0,0,0.7)",
      color: "white",
      borderRadius: 8,
      maxWidth: 300,
      fontSize: 14,
      zIndex: 9999,
    }}>
      {message}
    </div>
  );
};

export default GameMessageBox;
