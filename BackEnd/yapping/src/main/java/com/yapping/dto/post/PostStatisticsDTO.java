package com.yapping.dto.post;

public class PostStatisticsDTO {
    private Long totalPosts;
    private Long totalTextPosts;
    private Long totalResourcePosts;
    private Long publicPosts;
    private Long followersOnlyPosts;
    private Long privatePosts;
    private Long postsWithMedia;
    private Long totalLikes;
    private Long totalComments;
    private Long totalReposts;
    private Double averageInteraction;
    private Double publicPercentage;
    private Double followersOnlyPercentage;
    private Double privatePercentage;
    private Double textPostPercentage;
    private Double resourcePostPercentage;
    private Long todayPosts;
    private Long weekPosts;
    private Long monthPosts;

    // Constructors
    public PostStatisticsDTO() {}

    public PostStatisticsDTO(Long totalPosts, Long totalTextPosts, Long totalResourcePosts,
                           Long publicPosts, Long followersOnlyPosts, Long privatePosts,
                           Long postsWithMedia, Long totalLikes, Long totalComments,
                           Long totalReposts, Double averageInteraction,
                           Double publicPercentage, Double followersOnlyPercentage,
                           Double privatePercentage, Double textPostPercentage,
                           Double resourcePostPercentage, Long todayPosts,
                           Long weekPosts, Long monthPosts) {
        this.totalPosts = totalPosts;
        this.totalTextPosts = totalTextPosts;
        this.totalResourcePosts = totalResourcePosts;
        this.publicPosts = publicPosts;
        this.followersOnlyPosts = followersOnlyPosts;
        this.privatePosts = privatePosts;
        this.postsWithMedia = postsWithMedia;
        this.totalLikes = totalLikes;
        this.totalComments = totalComments;
        this.totalReposts = totalReposts;
        this.averageInteraction = averageInteraction;
        this.publicPercentage = publicPercentage;
        this.followersOnlyPercentage = followersOnlyPercentage;
        this.privatePercentage = privatePercentage;
        this.textPostPercentage = textPostPercentage;
        this.resourcePostPercentage = resourcePostPercentage;
        this.todayPosts = todayPosts;
        this.weekPosts = weekPosts;
        this.monthPosts = monthPosts;
    }

    // Getters and Setters
    public Long getTotalPosts() {
        return totalPosts;
    }

    public void setTotalPosts(Long totalPosts) {
        this.totalPosts = totalPosts;
    }

    public Long getTotalTextPosts() {
        return totalTextPosts;
    }

    public void setTotalTextPosts(Long totalTextPosts) {
        this.totalTextPosts = totalTextPosts;
    }

    public Long getTotalResourcePosts() {
        return totalResourcePosts;
    }

    public void setTotalResourcePosts(Long totalResourcePosts) {
        this.totalResourcePosts = totalResourcePosts;
    }

    public Long getPublicPosts() {
        return publicPosts;
    }

    public void setPublicPosts(Long publicPosts) {
        this.publicPosts = publicPosts;
    }

    public Long getFollowersOnlyPosts() {
        return followersOnlyPosts;
    }

    public void setFollowersOnlyPosts(Long followersOnlyPosts) {
        this.followersOnlyPosts = followersOnlyPosts;
    }

    public Long getPrivatePosts() {
        return privatePosts;
    }

    public void setPrivatePosts(Long privatePosts) {
        this.privatePosts = privatePosts;
    }

    public Long getPostsWithMedia() {
        return postsWithMedia;
    }

    public void setPostsWithMedia(Long postsWithMedia) {
        this.postsWithMedia = postsWithMedia;
    }

    public Long getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(Long totalLikes) {
        this.totalLikes = totalLikes;
    }

    public Long getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(Long totalComments) {
        this.totalComments = totalComments;
    }

    public Long getTotalReposts() {
        return totalReposts;
    }

    public void setTotalReposts(Long totalReposts) {
        this.totalReposts = totalReposts;
    }

    public Double getAverageInteraction() {
        return averageInteraction;
    }

    public void setAverageInteraction(Double averageInteraction) {
        this.averageInteraction = averageInteraction;
    }

    public Double getPublicPercentage() {
        return publicPercentage;
    }

    public void setPublicPercentage(Double publicPercentage) {
        this.publicPercentage = publicPercentage;
    }

    public Double getFollowersOnlyPercentage() {
        return followersOnlyPercentage;
    }

    public void setFollowersOnlyPercentage(Double followersOnlyPercentage) {
        this.followersOnlyPercentage = followersOnlyPercentage;
    }

    public Double getPrivatePercentage() {
        return privatePercentage;
    }

    public void setPrivatePercentage(Double privatePercentage) {
        this.privatePercentage = privatePercentage;
    }

    public Double getTextPostPercentage() {
        return textPostPercentage;
    }

    public void setTextPostPercentage(Double textPostPercentage) {
        this.textPostPercentage = textPostPercentage;
    }

    public Double getResourcePostPercentage() {
        return resourcePostPercentage;
    }

    public void setResourcePostPercentage(Double resourcePostPercentage) {
        this.resourcePostPercentage = resourcePostPercentage;
    }

    public Long getTodayPosts() {
        return todayPosts;
    }

    public void setTodayPosts(Long todayPosts) {
        this.todayPosts = todayPosts;
    }

    public Long getWeekPosts() {
        return weekPosts;
    }

    public void setWeekPosts(Long weekPosts) {
        this.weekPosts = weekPosts;
    }

    public Long getMonthPosts() {
        return monthPosts;
    }

    public void setMonthPosts(Long monthPosts) {
        this.monthPosts = monthPosts;
    }
}
