import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || "http://localhost:8080/api";

// 백엔드 서버 상태 확인
export const checkBackendStatus = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/health`, {
      timeout: 5000,
      headers: {
        'X-Requested-With': 'XMLHttpRequest'
      }
    });
    return {
      isOnline: true,
      status: response.status,
      data: response.data
    };
  } catch (error) {
    console.error('백엔드 서버 상태 확인 실패:', error);
    return {
      isOnline: false,
      error: error.message,
      status: error.response?.status || 0
    };
  }
};

// 백엔드 서버 상태에 따른 메시지 반환
export const getBackendStatusMessage = (status) => {
  if (status.isOnline) {
    return {
      type: 'success',
      message: '백엔드 서버가 정상적으로 연결되었습니다.',
      canProceed: true
    };
  }

  switch (status.status) {
    case 0:
      return {
        type: 'error',
        message: '백엔드 서버에 연결할 수 없습니다. 서버가 실행 중인지 확인해주세요.',
        canProceed: false,
        suggestion: '백엔드 개발자에게 서버 상태를 문의하세요.'
      };
    case 404:
      return {
        type: 'warning',
        message: '백엔드 API 엔드포인트가 준비되지 않았습니다.',
        canProceed: false,
        suggestion: '백엔드 개발자에게 API 구현 상태를 문의하세요.'
      };
    case 500:
      return {
        type: 'error',
        message: '백엔드 서버에서 내부 오류가 발생했습니다.',
        canProceed: false,
        suggestion: '백엔드 개발자에게 서버 로그를 확인해달라고 요청하세요.'
      };
    default:
      return {
        type: 'error',
        message: `백엔드 서버 연결에 문제가 있습니다. (상태 코드: ${status.status})`,
        canProceed: false,
        suggestion: '백엔드 개발자에게 문의하세요.'
      };
  }
};

// API 요청 실패 시 사용자 친화적 메시지 생성
export const getApiErrorMessage = (error) => {
  if (error.response?.data?.message) {
    return error.response.data.message;
  }

  if (error.code === 'ERR_NETWORK') {
    return '네트워크 연결에 문제가 있습니다. 인터넷 연결을 확인해주세요.';
  }

  if (error.code === 'ECONNABORTED') {
    return '요청 시간이 초과되었습니다. 잠시 후 다시 시도해주세요.';
  }

  if (error.response?.status === 404) {
    return '요청한 리소스를 찾을 수 없습니다.';
  }

  if (error.response?.status === 500) {
    return '서버에서 오류가 발생했습니다. 잠시 후 다시 시도해주세요.';
  }

  return '알 수 없는 오류가 발생했습니다.';
};

// 백엔드 연결 상태를 로컬 스토리지에 저장
export const saveBackendStatus = (status) => {
  try {
    localStorage.setItem('backendStatus', JSON.stringify({
      ...status,
      timestamp: Date.now()
    }));
  } catch (error) {
    console.error('백엔드 상태 저장 실패:', error);
  }
};

// 로컬 스토리지에서 백엔드 연결 상태 가져오기
export const getBackendStatusFromStorage = () => {
  try {
    const status = localStorage.getItem('backendStatus');
    if (status) {
      const parsedStatus = JSON.parse(status);
      // 5분 이내의 상태만 유효하다고 간주
      if (Date.now() - parsedStatus.timestamp < 5 * 60 * 1000) {
        return parsedStatus;
      }
    }
  } catch (error) {
    console.error('백엔드 상태 로드 실패:', error);
  }
  return null;
};

