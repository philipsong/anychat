
//��Ƶ��������������
var code_rate = ["����ģʽ", "40", "60", "100", "150", "200", "300", "400", "500", "600", "800", "1000", "1200", "1500"]; // ����������
var quality = ["�ϲ�����", "һ������", "�е�����", "�Ϻ�����", "�������"]; // ����������
var distinguishability = ["176x144", "320x240", "352x288", "640x480", "720x480", "720x576", "800x600", "960x720", "1024x576", "1280x720", "1280x1024", "1920x1080"]; // �ֱ���������
var frame_rate = ["5 FPS", "8 FPS", "12 FPS", "15 FPS", "20 FPS", "25 FPS", "30 FPS"]; // ֡��������
var preinstall = ["1", "2", "3", "4", "5"]; // Ԥ��������
var speakmode = ["����ģʽ(Ĭ��)", "�Ÿ�ģʽ", "����OKģʽ", "��·����ģʽ"]; // ��Ƶģʽ������

//���������ֵ
function filltheselect(id, theArray) {
    GetID(id).options.length = 0;
    for (var j = 0; j < theArray.length; j++) {
        var option = document.createElement("option");
        GetID(id).appendChild(option);
        option.value = j;
        option.text = theArray[j];
    }
}
// ��ʼ���߼����ý������пؼ� ���и�ֵ
function InitAdvanced() {
    filltheselect("code_rate", code_rate); // �������������
    filltheselect("quality", quality); // �������������
    filltheselect("distinguishability", distinguishability); // ���ֱ���������
    filltheselect("frame_rate", frame_rate); // ���֡��������
    filltheselect("preinstall", preinstall); // ���Ԥ��������
    filltheselect("Speak_Mode", speakmode); // ����ģʽ������
    filltheselect("DeviceType_VideoCapture", BRAC_EnumDevices(BRAC_DEVICE_VIDEOCAPTURE)); // ��Ƶ�ɼ��豸������ֵ
    filltheselect("DeviceType_AudioCapture", BRAC_EnumDevices(BRAC_DEVICE_AUDIOCAPTURE)); // ��Ƶ�ɼ��豸������ֵ
    filltheselect("DeviceType_AudioPlayBack", BRAC_EnumDevices(BRAC_DEVICE_AUDIOPLAYBACK)); // ��Ƶ�����豸������ֵ
    SetThePos();
    GetCurrentDevice();
}
// ����Ƶ�豸 ��ť����Ч��
function SettingBtnMouseout(id) {
    if (GetID(id).getAttribute("clickstate") == "false") // û�б�����İ�ť�ı䱳��ɫ
        GetID(id).style.backgroundColor = "#9CAAC1";
}
// ����Ƶ�豸 ��ť����Ч��
function SettingBtnMouseover(id, dd) {
    // �������в�������
    GetID("Device_Interface").style.display = "none";
    GetID("Video_Parameter_Interface").style.display = "none";
    GetID("Sound_Parameter_Interface").style.display = "none";
    GetID("Other_Parameter_Interface").style.display = "none";
    // ����ĸ���ť  ���³�ʼ��
    var btn = GetID("advanceset_div_Div_Btn").getElementsByTagName("div");
    for (var i = 0; i < btn.length; i++) {
        btn[i].style.backgroundColor = "#9CAAC1"; // ���ð�ť��ɫ
        btn[i].setAttribute("clickstate", "false"); // ���ð�ť���״̬Ϊδ���
    }
    GetID(dd).setAttribute("clickstate", "true"); // ���ñ�����İ�ť״̬Ϊ�����
    GetID(dd).style.backgroundColor = "White"; // ���ð�ť����ɫ
    GetID(id).style.display = "block"; // ��ʾ��ť��Ӧ�Ĳ�������
}

// �������¼�
function GetTheValue(id) {
    var value = GetID(id).options[GetID(id).selectedIndex].text;
    switch (id) {
        case "DeviceType_VideoCapture": // ��Ƶ�ɼ��豸
            BRAC_SelectVideoCapture(BRAC_DEVICE_VIDEOCAPTURE, value);
            break;
        case "DeviceType_AudioCapture": // ��Ƶ�ɼ��豸
            BRAC_SelectVideoCapture(BRAC_DEVICE_AUDIOCAPTURE, value);
            break;
        case "DeviceType_AudioPlayBack": // ��Ƶ�����豸
            BRAC_SelectVideoCapture(BRAC_DEVICE_AUDIOPLAYBACK, value);
            break;
        case "quality": // ����
            BRAC_SetSDKOption(BRAC_SO_LOCALVIDEO_QUALITYCTRL, value);
            break;
        case "code_rate": // ����
            if (value == "����ģʽ")
                GetID("quality").disabled = "";
            else {
                BRAC_SetSDKOption(BRAC_SO_LOCALVIDEO_BITRATECTRL, parseInt(value));
                GetID("quality").disabled = "disabled";
            }
            break;
        case "distinguishability": // �ֱ���
            var resolution = value.split('x');
            BRAC_SetSDKOption(BRAC_SO_LOCALVIDEO_WIDTHCTRL, parseInt(resolution[0]));
            BRAC_SetSDKOption(BRAC_SO_LOCALVIDEO_HEIGHTCTRL, parseInt(resolution[1]));
            //GetID("current_resolution").innerHTML = BRAC_GetSDKOptionInt(BRAC_SO_LOCALVIDEO_WIDTHCTRL) + "x" + BRAC_GetSDKOptionInt(BRAC_SO_LOCALVIDEO_HEIGHTCTRL) + ")";
            break;
        case "frame_rate": // ֡��
            BRAC_SetSDKOption(BRAC_SO_LOCALVIDEO_FPSCTRL, parseInt(value));
            break;
        case "preinstall": // Ԥ��
            BRAC_SetSDKOption(BRAC_SO_LOCALVIDEO_PRESETCTRL, parseInt(value));
            break;
        case "Speak_Mode": // ��Ƶ����ģʽ
            if (value == "����ģʽ(Ĭ��)")
                BRAC_SetSDKOption(BRAC_SO_AUDIO_CAPTUREMODE, 0);
            if (value == "�Ÿ�ģʽ")
                BRAC_SetSDKOption(BRAC_SO_AUDIO_CAPTUREMODE, 1);
            if (value == "����OKģʽ")
                BRAC_SetSDKOption(BRAC_SO_AUDIO_CAPTUREMODE, 2);
            if (value == "��·����ģʽ")
                BRAC_SetSDKOption(BRAC_SO_AUDIO_CAPTUREMODE, 3);
            break;
    }
}
//  ��ѡ���¼�
function ChangeTheResult(id) {
    switch (id) {
        case "ServerSetting": // ���������ò�����ť
            var GetAControl = GetID("advanceset_div_Tab").getElementsByTagName("a");
            var SelectTag = GetID("Video_Parameter_Interface").getElementsByTagName("select"); // ȡ�� ��Ƶ�������� ���� ����select��ǩ
            if (GetID("ServerSetting").checked == true) { // ��ǩ����¼�
                for (var i = 0; i < SelectTag.length; i++) { // ѭ����ǩ
                    SelectTag[i].disabled = "disabled";
                }
                for (var j = 0; j < GetAControl.length; j++)
                    GetAControl[j].style.color = "#999999";
            }
            else {
                for (var i = 0; i < SelectTag.length; i++) { // ѭ����ǩ
                    if (SelectTag[i].id != "quality") { //���� �������渴ѡ��ѡ�ж�����
                        SelectTag[i].disabled = "";
                        if (GetID("code_rate").options[GetID("code_rate").selectedIndex].text == "����ģʽ")
                            GetID("quality").disabled = "";
                    }
                    for (var j = 0; j < GetAControl.length; j++)
                        GetAControl[j].style.color = "Black";
                }
            }
            break;
        case "Checkbox_P2P":
            if (GetID(id).checked == true) BRAC_SetSDKOption(BRAC_SO_NETWORK_P2PPOLITIC, 1);
            else BRAC_SetSDKOption(BRAC_SO_NETWORK_P2PPOLITIC, 0);
            break;
        case "audio_vadctrl": // �������
            if (GetID(id).checked == true) BRAC_SetSDKOption(BRAC_SO_NETWORK_P2PPOLITIC, 1);
            else BRAC_SetSDKOption(BRAC_SO_NETWORK_P2PPOLITIC, 0);
            break;
        case "audio_echoctrl": // ��������
            if (GetID(id).checked == true) BRAC_SetSDKOption(BRAC_SO_AUDIO_ECHOCTRL, 1);
            else BRAC_SetSDKOption(BRAC_SO_AUDIO_ECHOCTRL, 0);
            break;
        case "audio_nsctrl": // ��������
            if (GetID(id).checked == true) BRAC_SetSDKOption(BRAC_SO_AUDIO_NSCTRL, 1);
            else BRAC_SetSDKOption(BRAC_SO_AUDIO_NSCTRL, 0);
            break;
        case "audio_agcctrl": // �Զ�����
            if (GetID(id).checked == true) BRAC_SetSDKOption(BRAC_SO_AUDIO_AGCCTRL, 1);
            else BRAC_SetSDKOption(BRAC_SO_AUDIO_AGCCTRL, 0);
            break;
    }
}

// ��ȡ��ǰ����ֵ
function GetCurrentDevice() {
    GetIndex("DeviceType_VideoCapture", BRAC_GetCurrentDevice(1), "combobox"); // ��ǰʹ�õ���Ƶ�ɼ���
    GetIndex("DeviceType_AudioCapture", BRAC_GetCurrentDevice(2), "combobox"); // ��ǰʹ�õ���Ƶ�ɼ���
    GetIndex("DeviceType_AudioPlayBack", BRAC_GetCurrentDevice(3), "combobox"); // ��ǰʹ�õ���Ƶ������
    GetIndex("quality", BRAC_GetSDKOptionInt(BRAC_SO_LOCALVIDEO_QUALITYCTRL), "combobox"); // ��ǰʹ�õ���������
    GetIndex("code_rate", BRAC_GetSDKOptionInt(BRAC_SO_LOCALVIDEO_BITRATECTRL), "combobox"); // ��ǰʹ�õ����ʲ���
    GetIndex("distinguishability", BRAC_GetSDKOptionInt(BRAC_SO_LOCALVIDEO_WIDTHCTRL) + "x" + BRAC_GetSDKOptionInt(BRAC_SO_LOCALVIDEO_HEIGHTCTRL), "combobox"); // ��ǰʹ�õĵķֱ���
    GetIndex("frame_rate", BRAC_GetSDKOptionInt(BRAC_SO_LOCALVIDEO_FPSCTRL), "combobox"); // ��ǰʹ�õĵ�֡�ʲ���
    GetIndex("preinstall", BRAC_GetSDKOptionInt(BRAC_SO_LOCALVIDEO_PRESETCTRL), "combobox"); // ��ǰʹ�õ�Ԥ�����

    GetIndex("Speak_Mode", BRAC_GetSDKOptionInt(BRAC_SO_AUDIO_CAPTUREMODE), "combobox"); // ��ǰʹ�õ���Ƶ����ģʽ

    GetIndex("audio_vadctrl", BRAC_GetSDKOptionInt(BRAC_SO_AUDIO_VADCTRL), "checkbox") // ��ǰʹ�õľ������
    GetIndex("audio_echoctrl", BRAC_GetSDKOptionInt(BRAC_SO_AUDIO_ECHOCTRL), "checkbox") // ��ǰʹ�õĻ�������
    GetIndex("audio_nsctrl", BRAC_GetSDKOptionInt(BRAC_SO_AUDIO_NSCTRL), "checkbox") // ��ǰʹ�õ���������
    GetIndex("audio_agcctrl", BRAC_GetSDKOptionInt(BRAC_SO_AUDIO_AGCCTRL), "checkbox") // ��ǰʹ�õ��Զ�����

    GetIndex("Checkbox_P2P", BRAC_GetSDKOptionInt(BRAC_SO_AUDIO_VADCTRL), "checkbox") // P2P

    GetID("current_resolution").innerHTML = BRAC_GetSDKOptionInt(BRAC_SO_LOCALVIDEO_WIDTHCTRL) + "x" + BRAC_GetSDKOptionInt(BRAC_SO_LOCALVIDEO_HEIGHTCTRL) + ")";
}

// ���ÿؼ���ʼֵ
function GetIndex(control_id, value, type) {
    if (type == "combobox") { // ������
        var slt = GetID(control_id);
        for (var i = 0; i < slt.length; i++) {
            if (slt[i].text == value) {
                GetID(control_id).selectedIndex = i;
                break;
            }
        }
    }
    else { // ��ѡ��
        if (value == 1) // 1Ϊ�� 
            GetID(control_id).checked = true;
        else
            GetID(control_id).checked = false;
    }
}

// ���� �߼����ý��� x����
function SetThePos() {
    var TheBodyWidth = document.body.offsetWidth;
    GetID("advanceset_div").style.marginLeft = (TheBodyWidth - 464) / 2 + 87 + "px";
}
// ��ʾ �������  ����
function BtnAdjust() {
    BRAC_ShowLVProperty("");
}
// Ӧ������
function BtnApply() {
    BRAC_SetSDKOption(BRAC_SO_LOCALVIDEO_APPLYPARAM, 1);
    setTimeout(GetCurrentDevice, 500);
}