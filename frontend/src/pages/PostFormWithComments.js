// src/pages/PostFormWithComments.js
import React, { useState, useEffect, useContext } from "react";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import { AuthContext } from "../context/AuthContext";
import "../styles/PostFormWithComments.css";

const categoryMap = {
  general: 1,
  kbo: 2,
  NPB: 3,
  mlb: 4,
  amateur: 5,
};

const PostFormWithComments = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const isEdit = Boolean(id);
  const draftKey = isEdit ? `postDraft_${id}` : "postDraft_new";

  const { user } = useContext(AuthContext);
  const [form, setForm] = useState({ title: "", text: "", category: "general" });
  const [writer, setWriter] = useState("");
  const [loading, setLoading] = useState(false);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState("");

  // 로그인 체크 및 draft / 기존 글 불러오기
  useEffect(() => {
    if (!user) {
      alert("로그인이 필요합니다.");
      navigate("/login");
      return;
    }
    setWriter(user.Id);

    const savedDraft = localStorage.getItem(draftKey);
    if (savedDraft) setForm(prev => ({ ...prev, ...JSON.parse(savedDraft) }));

    if (isEdit) fetchPost(id);
  }, [id, isEdit, navigate, user]);

  const fetchPost = async (postId) => {
    try {
      setLoading(true);
      const res = await axios.get(`/api/board/${postId}`);
      const board = res.data.board;
      setForm({
        title: board.title || "",
        text: board.text || "",
        category: Object.entries(categoryMap).find(([, val]) => val === board.category)?.[0] || "general",
      });
      setComments(res.data.comments || []);
    } catch (err) {
      console.error(err);
      alert("글 불러오기 실패");
    } finally {
      setLoading(false);
    }
  };

  // draft 자동 저장
  useEffect(() => {
    const handler = setTimeout(() => {
      localStorage.setItem(draftKey, JSON.stringify(form));
    }, 500);
    return () => clearTimeout(handler);
  }, [form, draftKey]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  };

  const savePost = async () => {
    if (!user) { alert("로그인이 필요합니다."); return; }
    const { title, text, category } = form;
    if (!title.trim() || !text.trim()) { alert("제목과 내용을 입력해주세요."); return; }

    const requestData = { writer: user.Id, title: title.trim(), text: text.trim(), category: categoryMap[category] };
    try {
      setLoading(true);
      if (isEdit) {
        await axios.put(`/api/board/${id}`, requestData);
        alert("글이 수정되었습니다.");
      } else {
        await axios.post("/api/board", requestData);
        alert("글이 등록되었습니다.");
        setForm({ title: "", text: "", category: "general" });
      }
      localStorage.removeItem(draftKey);
      navigate("/kboBoard");
    } catch (err) {
      console.error(err);
      alert("글 저장 실패");
    } finally { setLoading(false); }
  };

  // 댓글 작성
  const addComment = async () => {
    if (!newComment.trim()) return;
    if (!user) { alert("로그인 후 작성 가능합니다."); return; }
    try {
      const res = await axios.post(`/api/board/${id}/comments`, { writer: parseInt(user.Id, 10), text: newComment.trim() });
      setComments(prev => [...prev, res.data]);
      setNewComment("");
    } catch (err) {
      console.error(err);
      alert("댓글 작성 실패");
    }
  };

  // 댓글 삭제
  const deleteComment = async (commentId, commentWriter) => {
    if (!user || parseInt(user.Id, 10) !== commentWriter) { alert("자신의 댓글만 삭제할 수 있습니다."); return; }
    if (!window.confirm("댓글을 삭제하시겠습니까?")) return;
    try {
      await axios.delete(`/api/board/${id}/comments/${commentId}`);
      setComments(prev => prev.filter(c => c.id !== commentId));
    } catch (err) {
      console.error(err);
      alert("댓글 삭제 실패");
    }
  };

  return (
    <div className="post-form-container">
      <div className="post-form-header">
        <h1 className="post-form-title">{isEdit ? "글 수정" : "글쓰기"}</h1>
        <p className="post-form-subtitle">자유롭게 의견을 나누어보세요</p>
      </div>
      
      <div className="post-form-content">
        {loading && <div className="loading-message">로딩 중...</div>}
        
        <div className="form-section">
          <div className="form-group">
            <label className="form-label">제목</label>
            <input 
              type="text" 
              name="title" 
              value={form.title} 
              onChange={handleChange} 
              placeholder="제목을 입력하세요" 
              className="form-input"
            />
          </div>
          
          <div className="form-group">
            <label className="form-label">작성자</label>
            <input 
              type="text" 
              value={writer} 
              readOnly 
              className="form-input"
            />
          </div>
          
          <div className="form-group">
            <label className="form-label">카테고리</label>
            <select 
              name="category" 
              value={form.category} 
              onChange={handleChange}
              className="form-select"
            >
              <option value="general">자유</option>
              <option value="kbo">KBO</option>
              <option value="NPB">NPB</option>
              <option value="mlb">MLB</option>
              <option value="amateur">사회인야구</option>
            </select>
          </div>
          
          <div className="form-group">
            <label className="form-label">내용</label>
            <textarea 
              name="text" 
              value={form.text} 
              onChange={handleChange} 
              rows={10} 
              placeholder="내용을 입력하세요" 
              className="form-textarea"
            />
          </div>
        </div>
        
        <div className="form-button-group">
          <button 
            onClick={savePost} 
            disabled={loading}
            className="form-button form-button-primary"
          >
            {loading ? "저장 중..." : (isEdit ? "수정" : "저장")}
          </button>
          <button 
            onClick={() => { 
              if (window.confirm("취소하면 임시 저장이 삭제됩니다.")) { 
                localStorage.removeItem(draftKey); 
                navigate("/kboBoard"); 
              }
            }} 
            disabled={loading}
            className="form-button form-button-secondary"
          >
            취소
          </button>
        </div>

        {/* 댓글 */}
        {isEdit && (
          <div className="comments-section">
            <h3 className="comments-title">댓글</h3>
            <div className="new-comment">
              <textarea 
                value={newComment} 
                onChange={(e) => setNewComment(e.target.value)} 
                rows={3} 
                placeholder="댓글을 입력하세요" 
              />
              <button onClick={addComment}>작성</button>
            </div>
            <div className="comment-list">
              {comments.map((c) => (
                <div key={c.id} className="comment-item">
                  <p className="comment-text">{c.text}</p>
                  <div className="comment-meta">
                    <span className="comment-writer">{c.writer}</span>
                    <span className="comment-date">{c.createdAt}</span>
                  </div>
                  {user && parseInt(user.Id, 10) === c.writer && (
                    <div className="comment-actions">
                      <button 
                        onClick={() => deleteComment(c.id, c.writer)}
                        className="comment-button comment-button-delete"
                      >
                        삭제
                      </button>
                    </div>
                  )}
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default PostFormWithComments;
