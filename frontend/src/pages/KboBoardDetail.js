import React, { useState, useEffect, useContext, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import axios from 'axios';
import '../styles/kboboard.css';

const categoryTextMap = { 1:"자유",2:"KBO",3:"NPB",4:"MLB",5:"사회인야구" };

const KboBoardDetail = () => {
  const { user } = useContext(AuthContext);
  const { id } = useParams(); // 게시글 번호
  const navigate = useNavigate();

  const [board, setBoard] = useState(null);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');
  const [editingCommentId, setEditingCommentId] = useState(null);
  const [editingCommentText, setEditingCommentText] = useState('');
  const [loading, setLoading] = useState(true);

  const commentsEndRef = useRef(null);

  const scrollToBottom = () => {
    commentsEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  // 게시글 + 댓글 불러오기
  const fetchBoardDetail = async () => {
    try {
      const res = await axios.get(`http://localhost:8080/api/board/${id}`);
      setBoard(res.data.board);
      setComments(res.data.comments || []);
    } catch (err) {
      console.error(err);
      alert('게시글 로딩 실패');
    }
  };

  useEffect(() => {
    setLoading(true);
    fetchBoardDetail().finally(() => setLoading(false));

    const interval = setInterval(fetchBoardDetail, 5000); // 5초마다 댓글 갱신
    return () => clearInterval(interval);
  }, [id]);

  useEffect(() => {
    scrollToBottom();
  }, [comments]);

  // 댓글 작성
  const handleCommentSubmit = async () => {
    if (!user) {
      alert('로그인 후 작성 가능합니다.');
      return;
    }
    if (!newComment.trim()) return;

    try {
      await axios.post(`http://localhost:8080/api/comment`, {
        boardNo: id,
        writer: user.Id,
        text: newComment.trim(),
      }, { headers: { 'Content-Type': 'application/json' } });

      setNewComment('');
      fetchBoardDetail(); // 댓글 목록 갱신
    } catch (err) {
      console.error(err);
      alert('댓글 작성 실패');
    }
  };

  // 댓글 삭제
  const handleCommentDelete = async (commentId, commentWriter) => {
    if (!user || user.Id !== commentWriter) {
      alert('자신의 댓글만 삭제 가능합니다.');
      return;
    }
    if (!window.confirm('댓글을 삭제하시겠습니까?')) return;

    try {
      await axios.delete(`http://localhost:8080/api/comment/${id}/${commentId}`);
      setComments(prev => prev.filter(c => c.commentId !== commentId));
    } catch (err) {
      console.error(err);
      alert('댓글 삭제 실패');
    }
  };

  const startEditing = (commentId, text) => {
    setEditingCommentId(commentId);
    setEditingCommentText(text);
  };

  const saveEditComment = async () => {
    if (!editingCommentText.trim()) return;

    try {
      await axios.put(`http://localhost:8080/api/comment/${id}/${editingCommentId}`, {
        text: editingCommentText
      });
      setEditingCommentId(null);
      setEditingCommentText('');
      fetchBoardDetail();
    } catch (err) {
      console.error(err);
      alert('댓글 수정 실패');
    }
  };

  const cancelEdit = () => {
    setEditingCommentId(null);
    setEditingCommentText('');
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return '';
    return new Date(dateStr).toLocaleString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleCommentSubmit();
    }
  };

  if (loading) {
    return (
        <div className="board-detail-container">
          <div className="loading">게시글을 불러오는 중...</div>
        </div>
    );
  }

  if (!board) {
    return (
        <div className="board-detail-container">
          <div className="empty-state">게시글이 존재하지 않습니다.</div>
        </div>
    );
  }

  return (
      <div className="board-detail-container">
        <button className="back-button" onClick={() => navigate(-1)}>
          ← 목록으로
        </button>

        {/* 게시글 상세 */}
        <div className="board-detail-content">
          <div className="board-detail-header">
            <h1 className="board-detail-title">{board.title}</h1>
            <div className="board-detail-meta">
              <span>작성자: {board.writer}</span>
              <span>작성일: {formatDate(board.createdAt)}</span>
              <span>카테고리: {categoryTextMap[board.category] || board.category}</span>
            </div>
          </div>
          <div className="board-detail-body">
            {board.text}
          </div>
        </div>

        {/* 댓글 섹션 */}
        <div className="comments-section">
          <h3 style={{ marginBottom: '1.5rem', color: '#2c3e50' }}>
            댓글 ({comments.length})
          </h3>

          {/* 댓글 작성 */}
          <div className="comment-form">
          <textarea
              id="newComment"
              name="newComment"
              value={newComment}
              onChange={e => setNewComment(e.target.value)}
              onKeyPress={handleKeyPress}
              placeholder={user ? "댓글을 입력하세요 (Enter: 등록, Shift+Enter: 줄바꿈)" : "로그인 후 댓글 작성이 가능합니다."}
              rows={3}
              disabled={!user}
          />
            <button
                onClick={handleCommentSubmit}
                disabled={!user || !newComment.trim()}
            >
              댓글 작성
            </button>
            {!user && (
                <p style={{
                  color: '#718096',
                  fontSize: '0.9rem',
                  marginTop: '0.5rem'
                }}>
                  로그인 후 댓글 작성이 가능합니다.
                </p>
            )}
          </div>

          {/* 댓글 목록 */}
          <div className="comments-list">
            {comments.length === 0 ? (
                <div className="empty-state" style={{ margin: 0 }}>
                  댓글이 없습니다.
                </div>
            ) : (
                comments.map(c => (
                    <div key={c.commentId} className="comment-item">
                      {editingCommentId === c.commentId ? (
                          <div className="comment-edit">
                    <textarea
                        value={editingCommentText}
                        onChange={e => setEditingCommentText(e.target.value)}
                        rows={3}
                    />
                            <div className="comment-edit-actions">
                              <button className="save-btn" onClick={saveEditComment}>
                                저장
                              </button>
                              <button className="cancel-btn" onClick={cancelEdit}>
                                취소
                              </button>
                            </div>
                          </div>
                      ) : (
                          <>
                            <div className="comment-text">{c.text}</div>
                            <div className="comment-meta">
                      <span>
                        작성자: {c.writer} | {formatDate(c.date)}
                      </span>
                              {user && user.id === c.writer && (
                                  <div className="comment-actions">
                                    <button
                                        className="edit-btn"
                                        onClick={() => startEditing(c.commentId, c.text)}
                                    >
                                      수정
                                    </button>
                                    <button
                                        className="delete-btn"
                                        onClick={() => handleCommentDelete(c.commentId, c.writer)}
                                    >
                                      삭제
                                    </button>
                                  </div>
                              )}
                            </div>
                          </>
                      )}
                    </div>
                ))
            )}
            <div ref={commentsEndRef} />
          </div>
        </div>
      </div>
  );
};

export default KboBoardDetail;