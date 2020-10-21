/**
 * 
 */
function SetGridItemHeight() {
	let grid = document.getElementsByClassName('grid')[0];
	let rowHeight = parseInt(window.getComputedStyle(grid).getPropertyValue('grid-auto-rows'));
	let rowGap = parseInt(window.getComputedStyle(grid).getPropertyValue('grid-row-gap'));

	let item = grid.getElementsByClassName('item');
	for (let i = 0; i < item.length; ++i) {
		let a = Math.floor((item[i].children[0].offsetHeight) / 25)
		//item[i].style.gridRowEnd = `span ${Math.floor((item[i].children[0].offsetHeight) / 25)}`
		item[i].style.gridRowEnd = `span ` + a
	}
}

window.addEventListener("load", SetGridItemHeight);
window.addEventListener("resize", SetGridItemHeight);

function movePage(seq) {
	location.href = "http://"+location.host+"/Inphomation/detail?post_seq="+seq
}

//각종 페이지 기능들 구현하기
function test() {
	alert('테스형')
	location.href = "/test"
}


/*
  메인페이지에서 헤더의 인터렉트를 구현하는 함수 300의 고정값을 준 이유는 
  현재 메인페이지의 상단에서의 이미지의 크기를 400px로 고정을 해놨기 때문
*/
$(window).scroll(function (e) {
	var window = $(this).scrollTop()
	/*
	if (300 >= window) {
		$('.navbar').css('display', 'none')
	}else {
		$('.navbar').css('display', 'block')
	}
	*/
	//우측 하단의 상단방향 화살표가 400px이상 내려가면 화살표가 보이고 이하로 내려가면 안보이게 하는 역할 
	if(400 >= window){
		$('#screen-up').css('display','none')
	}else{
		$('#screen-up').css('display','block')	
	}
})

//우측 하단의 상단방향 화살표를 클릭시 실행되는 함수로 최상단페이지로 올려주는 역할 
$('#screen-up').click(function () {
	window.scrollTo({
		top: 0,
		left: 0
	})
})


//좋아요 클릭시
function clickLike(e, seq) {
	changeMark(e, 'like', seq)
}
//북마크 클릭시
function clickBookMark(e, seq) {
	changeMark(e, 'book', seq)
}


//좋아요와 북마크를 클릭했을시 숫자를 증감시키는 역할과 비동기를 통해서 DB에 추가 삭제작업함
function calculNumber(e, b, cate, seq) {
	let number = Number(e.innerText)
	let p = 'plus'
	let m = 'minus'
	
	//비동기로 DB에 갈때 필요한것 post_seq, 링크주소(pluslike, plusbook, minuslike, minusbook)	, 아이디
	if (b) { //빼줘야함
		e.innerText = --number
		$.ajax({
			url: m + cate,
			type: post,
			data: {
				postSeq: seq,
				//userSeq : 유저시퀀스번호
			},
			success: function () {
				alert('삭제 성')
			},
			error: function () {
				alert('삭제 삐뽀삐뽀')
			}
		})
	} else { //채우기
		e.innerText = ++number
		$.ajax({
			url: p + cate,
			type: post,
			data: {
				postSeq: seq,
				//userSeq : 유저시퀀스번호
			},
			success: function () {
				alert('추가 성')
			},
			error: function () {
				alert('추가 삐뽀삐뽀')
			}
		})
	}

}
//안이 차있으면 비워진 마크를 띄우고 비워져있으면 차있는 마크를 띄운다
function changeMark(e, cate, seq) {
	//fas == 안이 차있다 far==안이 비어있다
	//true false로 반환받음
	if (e.classList.contains('fas')) {
		//이미 누른거기때문에 비워야한다
		e.classList.remove('fas')
		e.classList.remove('text')
		e.classList.add('far')
		calculNumber(e, true, cate, seq)
	} else {
		//안누른거기 때문에 채워야한다
		e.classList.add('fas')
		e.classList.add('text')
		e.classList.remove('far')
		calculNumber(e, false, cate, seq)
	}
}

//메인의 searchBar
function searchKeywordMain() {
	//1.get으로 보내기
	keywordInput = document.getElementById('main-search-keyword')
	let keyword = keywordInput.value.trim()
	if (keyword == '') {
		//공백인 경우
		keywordInput.value = ''
		keywordInput.focus()
	} else {
		location.href = 'http://' + location.host + "/Inphomation/keywordSearch?keyworld=" + keyword
	}
}

function moveUserPage(e){
	location.href = 'http://' + location.host + "/Inphomation/mypage?user_seq="+e
}