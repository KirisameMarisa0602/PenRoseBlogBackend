package com.kirisamemarisa.blog.service.impl;

import com.kirisamemarisa.blog.common.ApiResponse;
import com.kirisamemarisa.blog.dto.*;
import com.kirisamemarisa.blog.model.*;
import com.kirisamemarisa.blog.repository.*;
import com.kirisamemarisa.blog.service.BlogPostService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional; import java.util.stream.Collectors; import java.util.List;

@Service public class BlogPostServiceImpl implements BlogPostService {
    private final BlogPostRepository blogPostRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BlogPostLikeRepository blogPostLikeRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserProfileRepository userProfileRepository;

    public BlogPostServiceImpl(BlogPostRepository blogPostRepository,
                               UserRepository userRepository,
                               CommentRepository commentRepository,
                               BlogPostLikeRepository blogPostLikeRepository,
                               CommentLikeRepository commentLikeRepository,
                               UserProfileRepository userProfileRepository) {
        this.blogPostRepository = blogPostRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.blogPostLikeRepository = blogPostLikeRepository;
        this.commentLikeRepository = commentLikeRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    @Transactional
    public ApiResponse<Long> create(BlogPostCreateDTO dto) {
        if (dto == null) return new ApiResponse<>(400, "请求体不能为空", null);
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty())
            return new ApiResponse<>(400, "标题不能为空", null);
        if (dto.getContent() == null || dto.getContent().trim().isEmpty())
            return new ApiResponse<>(400, "正文不能为空", null);
        if (dto.getUserId() == null)
            return new ApiResponse<>(400, "用户ID不能为空", null);
        Optional<User> userOpt = userRepository.findById(dto.getUserId());
        if (userOpt.isEmpty())
            return new ApiResponse<>(404, "用户不存在", null);

        BlogPost post = new BlogPost();
        post.setTitle(dto.getTitle().trim());
        post.setContent(dto.getContent().trim());
        post.setCoverImageUrl(dto.getCoverImageUrl());
        post.setDirectory(dto.getDirectory());
        post.setUser(userOpt.get());
        post.setRepost(false);
        BlogPost saved = blogPostRepository.save(post);
        return new ApiResponse<>(200, "创建成功", saved.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public BlogPostDTO getById(Long id) {
        return blogPostRepository.findById(id).map(p -> toDTO(p, null)).orElse(null);
    }

    @Override
    @Transactional
    public boolean update(Long id, BlogPostUpdateDTO dto) {
        if (dto == null) return false;
        Optional<BlogPost> opt = blogPostRepository.findById(id);
        if (opt.isEmpty()) return false;
        BlogPost post = opt.get();
        if (dto.getCoverImageUrl() != null) post.setCoverImageUrl(dto.getCoverImageUrl());
        if (dto.getContent() != null && !dto.getContent().trim().isEmpty())
            post.setContent(dto.getContent().trim());
        if (dto.getDirectory() != null) post.setDirectory(dto.getDirectory());
        blogPostRepository.save(post);
        return true;
    }

    @Override
    @Transactional
    public ApiResponse<Boolean> toggleLike(Long blogPostId, Long userId) {
        if (blogPostId == null || userId == null)
            return new ApiResponse<>(400, "参数缺失", false);
        Optional<BlogPost> postOpt = blogPostRepository.findById(blogPostId);
        if (postOpt.isEmpty()) return new ApiResponse<>(404, "博客不存在", false);
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return new ApiResponse<>(404, "用户不存在", false);

        BlogPost post = postOpt.get();
        Optional<BlogPostLike> likeOpt = blogPostLikeRepository.findByBlogPostIdAndUserId(blogPostId, userId);
        if (likeOpt.isPresent()) {
            blogPostLikeRepository.delete(likeOpt.get());
            post.setLikeCount(safeLong(post.getLikeCount()) - 1);
            blogPostRepository.save(post);
            return new ApiResponse<>(200, "已取消点赞", false);
        } else {
            BlogPostLike like = new BlogPostLike();
            like.setBlogPost(post);
            like.setUser(userOpt.get());
            blogPostLikeRepository.save(like);
            post.setLikeCount(safeLong(post.getLikeCount()) + 1);
            blogPostRepository.save(post);
            return new ApiResponse<>(200, "点赞成功", true);
        }
    }

    @Override
    @Transactional
    public ApiResponse<Boolean> toggleCommentLike(Long commentId, Long userId) {
        if (commentId == null || userId == null)
            return new ApiResponse<>(400, "参数缺失", false);
        Optional<Comment> cOpt = commentRepository.findById(commentId);
        if (cOpt.isEmpty()) return new ApiResponse<>(404, "评论不存在", false);
        Optional<User> uOpt = userRepository.findById(userId);
        if (uOpt.isEmpty()) return new ApiResponse<>(404, "用户不存在", false);

        Comment comment = cOpt.get();
        Optional<CommentLike> likeOpt = commentLikeRepository.findByCommentIdAndUserId(commentId, userId);
        if (likeOpt.isPresent()) {
            commentLikeRepository.delete(likeOpt.get());
            comment.setLikeCount(safeLong(comment.getLikeCount()) - 1);
            commentRepository.save(comment);
            return new ApiResponse<>(200, "已取消点赞", false);
        } else {
            CommentLike cl = new CommentLike();
            cl.setComment(comment);
            cl.setUser(uOpt.get());
            commentLikeRepository.save(cl);
            comment.setLikeCount(safeLong(comment.getLikeCount()) + 1);
            commentRepository.save(comment);
            return new ApiResponse<>(200, "点赞成功", true);
        }
    }

    @Override
    @Transactional
    public ApiResponse<Long> addComment(CommentCreateDTO dto) {
        if (dto == null) return new ApiResponse<>(400, "请求体不能为空", null);
        if (dto.getBlogPostId() == null) return new ApiResponse<>(400, "博客ID不能为空", null);
        if (dto.getUserId() == null) return new ApiResponse<>(400, "用户ID不能为空", null);
        if (dto.getContent() == null || dto.getContent().trim().isEmpty())
            return new ApiResponse<>(400, "评论内容不能为空", null);
        Optional<BlogPost> postOpt = blogPostRepository.findById(dto.getBlogPostId());
        if (postOpt.isEmpty()) return new ApiResponse<>(404, "博客不存在", null);
        Optional<User> userOpt = userRepository.findById(dto.getUserId());
        if (userOpt.isEmpty()) return new ApiResponse<>(404, "用户不存在", null);

        Comment c = new Comment();
        c.setBlogPost(postOpt.get());
        c.setUser(userOpt.get());
        c.setContent(dto.getContent().trim());
        Comment saved = commentRepository.save(c);

        BlogPost post = postOpt.get();
        post.setCommentCount(safeLong(post.getCommentCount()) + 1);
        blogPostRepository.save(post);

        return new ApiResponse<>(200, "评论成功", saved.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDTO> listComments(Long blogPostId, Long currentUserId) {
        return commentRepository.findByBlogPostIdOrderByCreatedAtDesc(blogPostId)
                .stream().map(c -> toCommentDTO(c, currentUserId)).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogPostDTO> list(int page, int size, Long currentUserId) {
        return blogPostRepository.findAll(PageRequest.of(page, size))
                .stream().map(p -> toDTO(p, currentUserId)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ApiResponse<Long> repost(RepostCreateDTO dto) {
        if (dto == null) return new ApiResponse<>(400, "请求体不能为空", null);
        if (dto.getOriginalPostId() == null) return new ApiResponse<>(400, "原博客ID不能为空", null);
        if (dto.getUserId() == null) return new ApiResponse<>(400, "用户ID不能为空", null);

        Optional<BlogPost> originalOpt = blogPostRepository.findById(dto.getOriginalPostId());
        if (originalOpt.isEmpty()) return new ApiResponse<>(404, "原博客不存在", null);
        Optional<User> userOpt = userRepository.findById(dto.getUserId());
        if (userOpt.isEmpty()) return new ApiResponse<>(404, "用户不存在", null);

        BlogPost original = originalOpt.get();

        // 原博客正文用于生成转发标题和内容片段
        String originalContent = original.getContent() != null ? original.getContent() : "";
        String snippet = originalContent.length() > 30 ? originalContent.substring(0, 30) : originalContent;

        BlogPost repost = new BlogPost();
        repost.setUser(userOpt.get());
        repost.setRepost(true);
        repost.setOriginalPost(original);
        repost.setTitle(snippet);          // 标题为原文前30字
        repost.setContent(snippet);        // 内容只存片段（与需求“只有正文片段”一致）
        repost.setCoverImageUrl(null);     // 转发不保留封面
        repost.setDirectory(null);         // 不继承目录（需求未要求）
        BlogPost saved = blogPostRepository.save(repost);

        // 原博客转发计数自增（安全处理 null）
        original.setRepostCount(safeInt(original.getRepostCount()) + 1);
        blogPostRepository.save(original);

        return new ApiResponse<>(200, "转发成功", saved.getId());
    }

    private BlogPostDTO toDTO(BlogPost post, Long currentUserId) {
        BlogPostDTO dto = new BlogPostDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setUserId(post.getUser() != null ? post.getUser().getId() : null);
        dto.setCoverImageUrl(post.getCoverImageUrl());
        dto.setContent(post.getContent());
        dto.setDirectory(post.getDirectory());
        dto.setLikeCount(safeLong(post.getLikeCount()));
        dto.setCommentCount(safeLong(post.getCommentCount()));
        dto.setShareCount(safeLong(post.getShareCount()));
        dto.setRepostCount(safeInt(post.getRepostCount()));
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setRepost(post.isRepost());
        dto.setOriginalPostId(post.getOriginalPost() != null ? post.getOriginalPost().getId() : null);
        if (currentUserId != null) {
            dto.setLikedByCurrentUser(
                    blogPostLikeRepository.findByBlogPostIdAndUserId(post.getId(), currentUserId).isPresent()
            );
        }
        return dto;
    }

    private CommentDTO toCommentDTO(Comment c, Long currentUserId) {
        CommentDTO dto = new CommentDTO();
        dto.setId(c.getId());
        dto.setBlogPostId(c.getBlogPost().getId());
        dto.setUserId(c.getUser().getId());
        dto.setContent(c.getContent());
        dto.setCreatedAt(c.getCreatedAt());
        dto.setLikeCount(safeLong(c.getLikeCount()));
        if (currentUserId != null) {
            dto.setLikedByCurrentUser(
                    commentLikeRepository.findByCommentIdAndUserId(c.getId(), currentUserId).isPresent()
            );
        }
        userProfileRepository.findById(c.getUser().getId()).ifPresent(p -> {
            dto.setNickname(p.getNickname());
            dto.setAvatarUrl(p.getAvatarUrl());
        });
        return dto;
    }

    private long safeLong(Long v) { return v == null ? 0L : v; }
    private int safeInt(Integer v) { return v == null ? 0 : v; }

}