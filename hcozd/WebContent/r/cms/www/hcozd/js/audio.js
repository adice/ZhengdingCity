$(".audio1").mousedown(function(){
	$(".audio_img").attr('src', '/hcozd/r/cms/www/hcozd/img/audio_2.png');
    // 获取录音机
    HZRecorder.get(function (rec) {
        recorder = rec;
        // 开始录音
        recorder.start();
    });
});
$(".audio1").mouseup(function(){
	$(".audio_img").attr('src', '/hcozd/r/cms/www/hcozd/img/audio.png');
    recorder.stop();
    // 上传
    recorder.upload("/hcozd/baiduyuyin", function (txt) {
    	$("#searchTxt").val(txt);
    });
});
