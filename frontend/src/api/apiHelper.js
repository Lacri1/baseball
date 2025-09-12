export const getApiMessage = (resOrErr) => {
  if (!resOrErr) return "알 수 없는 오류";

  if (resOrErr.response) {
    return resOrErr.response.data?.message || "알 수 없는 서버 오류";
  }

  return resOrErr.data?.message || "성공";
};
