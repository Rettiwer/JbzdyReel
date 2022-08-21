let isScrolling = false;

let volumeLevel = 1;
let isMutedVideo = true;

function fadeToNewBackground(newBgSrc, isVideo) {
    let reelsBg = document.querySelectorAll(".reels-bg")[0];

    let opacity = 1;
    let intervalID = setInterval(function () {
        if (opacity > 0) {
            opacity = opacity - 0.1
            reelsBg.style.opacity = opacity;
        } else {
            let bgImg = reelsBg.querySelector('.bg-img');
            let bgPlayerNode = reelsBg.querySelector('.bg-player');

            if (!isVideo) {
                bgPlayerNode.style.display = "none";
                let img = reelsBg.querySelector('.bg-img');
                img.src = newBgSrc;
                bgImg.style.display = "block";
            } else {
                bgImg.style.display = "none";
                let bgPlayer = videojs(bgPlayerNode);

                bgPlayer.src(newBgSrc);

                bgPlayerNode.style.display = "block";
            }

            clearInterval(intervalID);
            intervalID = setInterval(function () {

                if (opacity < 1) {
                    opacity = opacity + 0.1;
                    reelsBg.style.opacity = opacity;
                } else {
                    clearInterval(intervalID);
                }
            }, 20);
        }
    }, 1);
}

function scrollToSmoothly(container, pos, time) {
    let currentPos = container.scrollTop;
    let start = null;
    if(time == null) time = 500;
    pos = +pos, time = +time;
    window.requestAnimationFrame(function step(currentTime) {
        start = !start ? currentTime : start;
        let progress = currentTime - start;
        if (currentPos < pos) {
            container.scrollTo(0, ((pos - currentPos) * progress / time) + currentPos);
        } else {
            container.scrollTo(0, currentPos - ((currentPos - pos) * progress / time));
            container.scrollTo(0, currentPos - ((currentPos - pos) * progress / time));
        }
        if (progress < time) {
            window.requestAnimationFrame(step);
        } else {
            container.scrollTo(0, pos);
            isScrolling = false;
        }
    });
}

/*
    Scroll to reel
    direction - true means up, false means down
*/

function scrollReel(direction) {
    if(isScrolling)
        return;

    let reels = document.querySelectorAll(".reel");
    let reelsCarousel = document.querySelectorAll(".reels-carousel")[0];

    let activeReelIndex = 0;
    for(activeReelIndex; activeReelIndex < reels.length; activeReelIndex++) {
        if(reels[activeReelIndex].classList.contains('active'))
            break;
    }

    let nextReel;
    if(direction)
        nextReel = reels[activeReelIndex - 1];
    else
        nextReel = reels[activeReelIndex + 1];


    if(nextReel === undefined) {
        return;
    }

    isScrolling = true;

    let nextReelVideo = nextReel.querySelector('.video-js');
    if(nextReelVideo  !== null) {
        nextReelVideo = videojs(nextReelVideo);
        fadeToNewBackground(nextReelVideo.currentSrc(), true);

        nextReelVideo.player().muted(isMutedVideo);
        nextReelVideo.player().volume(volumeLevel);

        nextReelVideo.player().play();

        nextReelVideo.on('volumechange', () => {
            volumeLevel = nextReelVideo.volume();
            isMutedVideo = nextReelVideo.muted();
        });
    }
    else {
        fadeToNewBackground(nextReel.getElementsByTagName("img")[0].src, false);
    }

    scrollToSmoothly(reelsCarousel, nextReel.offsetTop, 200);
    reels[activeReelIndex].classList.remove("active");
    nextReel.classList.add("active");

    let isCurrentReelVideo = reels[activeReelIndex].querySelector('.video-js');
    if (isCurrentReelVideo !==  null) {
        isCurrentReelVideo = videojs(isCurrentReelVideo);
        isCurrentReelVideo.player().pause();
    }

    if (reels.length - (activeReelIndex + 1 ) === 1) {
        loadReels();
    }
}

/*

    Detect key up and key down event

*/

document.addEventListener("keydown", arrowKeyEvent, false);

function arrowKeyEvent(e) {
    console.log(e.keyCode);
    if (e.keyCode === '38') {
        scrollReel(true);
    }
    else if (e.keyCode === '40') {
        scrollReel(false);
    }
}

/*

    Detect mouse wheel

*/

document.addEventListener("wheel", wheelEvent, true);

let index = 0;

function wheelEvent(e) {
    const delta = Math.sign(e.deltaY);
    index += delta;

    if(index >= 2) {
        scrollReel(false);
        index = 0;
    }
    else if (index <= -2) {
        scrollReel(true);
        index = 0;
    }
}

/*

    Detect touch scroll

*/

document.addEventListener('touchstart', handleTouchStart, false);
document.addEventListener('touchmove', handleTouchMove, false);

let xDown = null;
let yDown = null;

function getTouches(evt) {
    return evt.touches ||
        evt.originalEvent.touches;
}

function handleTouchStart(evt) {
    const firstTouch = getTouches(evt)[0];
    xDown = firstTouch.clientX;
    yDown = firstTouch.clientY;
}

function handleTouchMove(evt) {
    if ( ! xDown || ! yDown ) {
        return;
    }

    let xUp = evt.touches[0].clientX;
    let yUp = evt.touches[0].clientY;

    let xDiff = xDown - xUp;
    let yDiff = yDown - yUp;

    if ( Math.abs( xDiff ) < Math.abs( yDiff ) ) {
        if ( yDiff > 0 ) {
            scrollReel(false);
        } else {
            scrollReel(true);
        }
    }
    /* reset values */
    xDown = null;
    yDown = null;
}

/*

    Scroll to active reel on window resize

*/

window.addEventListener("resize", resizeEvent);

let resizingTimer;

function resizeEvent() {
    clearTimeout(resizingTimer);
    resizingTimer = setTimeout(function() {
        let activeReel = document.querySelectorAll(".reels-carousel .active")[0];

        activeReel.scrollIntoView();
    }, 50);
}


/*

    Load new reels

 */

let pageNo = 0;
function loadReels() {
    fetch('/reels?pageNo=' + pageNo).then(function(response) {
        response.json().then(function(reels){
            reels.forEach(function(reel){
                generateReelImage(reel.mediaUrl, reel.title, reel.createdAt, reel.mediaType);
            });
            pageNo += 1;
        });
    }).catch(err => console.error(err));
}

loadReels();

function generateReelImage(mediaUrl, title, time, mediaType) {
    let reelsCarousel = document.querySelector(".reels-carousel");

    const reel = document.createElement("article");
    reel.classList.add("reel");

    if (document.querySelector(".reels-carousel").childNodes.length === 1) {
        reel.classList.add("active");
    }

    const content = document.createElement("article");
    content.classList.add("content");

    const contentVideo = document.createElement("video");
    if (mediaType === "IMAGE") {
        const contentImg = document.createElement("img");
        contentImg.src = mediaUrl;
        content.appendChild(contentImg);
    }
    else if (mediaType === "VIDEO") {
        contentVideo.classList.add("video-js");
        contentVideo.setAttribute("preload", "auto");
        contentVideo.setAttribute("autoplay", "");
        contentVideo.setAttribute("muted", "");
        contentVideo.setAttribute("controls", "");
        contentVideo.setAttribute("loop", "");
        contentVideo.setAttribute("data-setup", "{}");

        const videoSource = document.createElement("source");
        videoSource.src = mediaUrl;
        videoSource.setAttribute("type", "video/mp4");


        contentVideo.appendChild(videoSource);

        content.appendChild(contentVideo);
    }

    reel.appendChild(content);

    const contentDesc = document.createElement("article");
    contentDesc.classList.add("content-description");

    const contentTitle = document.createElement("h2");
    contentTitle.append(title);
    contentDesc.appendChild(contentTitle);

    const contentTime = document.createElement("h3");

    contentTime.append(timeAgo(time));
    contentDesc.appendChild(contentTime);

    reel.appendChild(contentDesc);

    reelsCarousel.append(reel);

    if (mediaType === "VIDEO") {
        let newVideoPlayer = videojs(contentVideo);

        newVideoPlayer.on('click', () => {
            isMutedVideo = !isMutedVideo;
            newVideoPlayer.player().muted(isMutedVideo);
        });

        newVideoPlayer.on('touchstart', () => {
            isMutedVideo = !isMutedVideo;
            newVideoPlayer.player().muted(isMutedVideo);
        });
    }
}

function timeAgo(createdAt) {
    let parsedDate = Date.parse(createdAt);

    let now = new Date();

    let secs = Math.abs(now-parsedDate)/1000;
    let mins = parseInt(secs/60);
    let hours = parseInt(mins/60);
    let days = parseInt(hours/24);
    let months = parseInt(days/30);
    let years = parseInt(months/12);

    if (secs <= 60) {
        return  secs + " sekund temu";
    }
    else if (mins <= 60) {
        return  secs + " minut temu";
    }
    else if (hours <= 24) {
        return  hours + " godzin temu";
    }
    else if (days <= 30) {
        return days + " dni temu";
    }
    else if (months <= 12) {
        return months + " miesięcy temu";
    }
    else
        return years + " lat temu";
}

