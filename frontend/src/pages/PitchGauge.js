import React from "react";

export const PitchGauge = ({ value }) => (
  <div style={{ width: 200, height: 20, background: "#eee", marginTop: 10 }}>
    <div style={{ width: `${value}%`, height: "100%", background: "red" }} />
  </div>
);

export const SwingGauge = ({ value }) => (
  <div style={{ width: 200, height: 20, background: "#eee", marginTop: 10 }}>
    <div style={{ width: `${value}%`, height: "100%", background: "blue" }} />
  </div>
);
