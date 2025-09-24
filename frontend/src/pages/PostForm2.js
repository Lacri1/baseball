import React, { useState, useEffect, useContext } from "react";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import { AuthContext } from "../context/AuthContext";
import "../styles/PostForm2.css";

const categoryMap = {
  general: 1,
  kbo: 2,
  NPB: 3,
  mlb: 4,
  amateur: 5,
};

const PostForm2 = () => {
  const { user } = useContext(AuthContext);
  const navigate = useNavigate();
  const { id } = useParams(); // 글 번호
  const isEdit = Boolean(id);

  const [form, setForm] = useState({ title: "", text: "", category: "general" });
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState("");
  const [loading, setLoading] = useState(false);

  // 글/댓글 불러오기
  useEffect(() => {
    if (!user) {
      alert("로그인이 필요합니다.");
      navigate("/login");
      return;
    }

    if (isEdit) fetchPost(id);
  }, [id, isEdit, user, navigate]);

  const fetchPost = async (postId) => {
    try {
      setLoading(true);
      const res = await axios.get(`/api/board/${postId}`);
      const board = res.data.board;
      setForm({
        title: board.title || "",
        text: board.text || "",
        category:
          Object.entries(categoryMap).find(([, val]) => val === parseInt(board.category))?.[0] ||
          "general",
      });
      setComments(res.data.comments || []);
    } catch (err) {
      console.error(err);
      alert("글 불러오기 실패");
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  // 글 저장/수정
  const savePost = async () => {
    if (!form.title.trim() || !form.text.trim()) {
      alert("제목과 내용을 입력해주세요.");
      return;
    }

    const requestData = {
      writer: parseInt(user.Id, 10),
      title: form.title.trim(),
      text: form.text.trim(),
      category: categoryMap[form.category],
    };

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
      navigate("/kboBoard");
    } catch (err) {
      console.error(err);
      alert("글 저장 실패");
    } finally {
      setLoading(false);
    }
  };

  // 댓글 작성
  const addComment = async () => {
    if (!newComment.trim()) return;

    try {
      const res = await axios.post(`/api/comment`, {
        boardNo: isEdit ? parseInt(id, 10) : 0,
        writer: parseInt(user.Id, 10),
        text: newComment.trim(),
      });
      setComments((prev) => [...prev, res.data]);
      setNewComment("");
    } catch (err) {
      console.error(err);
      alert("댓글 작성 실패");
    }
  };

  // 댓글 삭제
  const deleteComment = async (commentId, commentWriter) => {
    if (parseInt(user.Id, 10) !== commentWriter) {
      alert("자신의 댓글만 삭제 가능합니다.");
      return;
    }
    if (!window.confirm("댓글을 삭제하시겠습니까?")) return;

    try {
      await axios.delete(`/api/comment/${commentId}`, {
        data: { boardNo: parseInt(id, 10) },
      });
      setComments((prev) => prev.filter((c) => c.commentId !== commentId));
    } catch (err) {
      console.error(err);
      alert("댓글 삭제 실패");
    }
  };

  return (
    <div className="container">
      <h2 style={{ textAlign: "center" }}>{isEdit ? "글 수정" : "글쓰기"}</h2>
      {loading && <p>로딩 중...</p>}

      <input
        type="text"
        name="title"
        value={form.title}
        onChange={handleChange}
        placeholder="제목"
      />

      <span>작성자</span>
      <input type="text" value={user?.Id || ""} readOnly />

      <label>
        종류{" "}
        <select name="category" value={form.category} onChange={handleChange}>
          <option value="general">자유</option>
          <option value="kbo">KBO</option>
          <option value="NPB">NPB</option>
          <option value="mlb">MLB</option>
          <option value="amateur">사회인야구</option>
        </select>
      </label>

      <textarea
        name="text"
        value={form.text}
        onChange={handleChange}
        rows={10}
        placeholder="내용"
      />

      <div className="button-group">
        <button onClick={savePost} disabled={loading}>
          {isEdit ? "수정" : "저장"}
        </button>
        <button
          onClick={() => {
            if (window.confirm("정말 취소하시겠습니까?")) navigate("/kboBoard");
          }}
          disabled={loading}
        >
          취소
        </button>
      </div>

      {/* 댓글 영역 */}
      {isEdit && (
        <div className="comments-section">
          <h3>댓글</h3>
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
              <div key={c.commentId} className="comment-item">
                <p>{c.text}</p>
                <small>
                  작성자: {c.writer} | {c.date || "작성 시간 없음"}
                </small>
                {parseInt(user.Id, 10) === c.writer && (
                  <button onClick={() => deleteComment(c.commentId, c.writer)}>삭제</button>
                )}
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default PostForm2;
