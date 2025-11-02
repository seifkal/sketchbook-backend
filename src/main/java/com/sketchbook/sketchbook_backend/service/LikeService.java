package com.sketchbook.sketchbook_backend.service;

import com.sketchbook.sketchbook_backend.entity.Like;
import com.sketchbook.sketchbook_backend.entity.Post;
import com.sketchbook.sketchbook_backend.entity.User;
import com.sketchbook.sketchbook_backend.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    public boolean toggleLike(Post post, User user) {
        Optional<Like> existing = likeRepository.findByPostAndUser(post, user);

        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
            return false; // disliked
        } else {
            Like like = new Like();
            like.setPost(post);
            like.setUser(user);
            likeRepository.save(like);
            return true; // liked
        }
    }

    public Long countLikes(Post post) {
        return likeRepository.countByPost(post);
    }

    public Boolean isLiked(Post post, User user) {
        return likeRepository.findByPostAndUser(post, user).isPresent();
    }

}
