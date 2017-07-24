using UnityEngine;
using System.Collections;

public class FreeCamera : MonoBehaviour {
	//旋转变量;
	private float m_deltX = 0f;
	private float m_deltY = 0f;
	//缩放变量;
	private float m_distance = 10f;
	private float m_mSpeed = 5f;
	//移动变量;
	private Vector3 m_mouseMovePos = Vector3.zero;
	// 起始位置
	private Vector3 m_startPos = new Vector3(0,0,-10);

	//初始位置
	private float camPostion_x = 0;
	private float camPostion_y = 0;

	void Start()
	{
//		GetComponent<Camera> ().transform.localPosition = m_startPos;//new Vector3(0, 0, -10);
//		GetComponent<Camera> ().transform.position = m_startPos;
	}

	void Update () {
		//鼠标右键点下控制相机旋转;
		if (Input.GetMouseButton(0))
		{
			m_deltX += Input.GetAxis("Mouse X") * m_mSpeed;
			m_deltY -= Input.GetAxis("Mouse Y") * m_mSpeed;
			m_deltX = ClampAngle(m_deltX, -360, 360);
			m_deltY = ClampAngle(m_deltY, -70, 70);
			GetComponent<Camera>().transform.rotation = Quaternion.Euler(m_deltY, m_deltX, 0);
		}

		//鼠标中键点下场景缩放;
		if (Input.GetAxis("Mouse ScrollWheel") != 0)
		{
			//自由缩放方式;
			m_distance = Input.GetAxis("Mouse ScrollWheel") * 10f;
			GetComponent<Camera>().transform.localPosition = GetComponent<Camera>().transform.position + GetComponent<Camera>().transform.forward * m_distance;
		}

		//中键按下平移
		if (Input.GetMouseButton(2))
		{
			camPostion_x -= Input.GetAxis("Mouse X") * 0.04f*m_mSpeed;
			camPostion_y -= Input.GetAxis("Mouse Y") * 0.04f*m_mSpeed;
			GetComponent<Camera>().transform.localPosition = new Vector3(camPostion_x, camPostion_y, 0);
		}

		//相机复位远点;
		if (Input.GetKey(KeyCode.Space))
		{
			m_distance = 10.0f;
			GetComponent<Camera> ().transform.position = m_startPos;
			GetComponent<Camera> ().transform.rotation = Quaternion.identity;
			m_deltX = 0f;
			m_deltY = 0f;
		}

		if (Input.GetKey (KeyCode.W)||Input.GetKey (KeyCode.UpArrow)) {
			GetComponent<Camera> ().transform.Translate (0, 0, m_mSpeed*0.1f, Space.Self);
		}
		if (Input.GetKey (KeyCode.S)||Input.GetKey (KeyCode.DownArrow)) {
			GetComponent<Camera> ().transform.Translate (0, 0, -m_mSpeed*0.1f, Space.Self);
		}
		if (Input.GetKey (KeyCode.A)||Input.GetKey (KeyCode.LeftArrow)) {
			GetComponent<Camera> ().transform.Translate (-m_mSpeed*0.1f, 0, 0, Space.Self);
		}
		if (Input.GetKey (KeyCode.D)||Input.GetKey (KeyCode.RightArrow)) {
			GetComponent<Camera> ().transform.Translate (m_mSpeed*0.1f, 0, 0, Space.Self);
		}
		if (Input.GetKeyDown (KeyCode.Q)) {
			GetComponent<Camera> ().transform.Translate (0, m_mSpeed*0.1f, 0, Space.Self);
		}
		if (Input.GetKeyDown (KeyCode.E)) {
			GetComponent<Camera> ().transform.Translate (0, -m_mSpeed*0.1f, 0, Space.Self);
		}
	
	}

	//规划角度;
	float ClampAngle(float angle, float minAngle, float maxAgnle)
	{
		if (angle <= -360)
			angle += 360;
		if (angle >= 360)
			angle -= 360;

		return Mathf.Clamp(angle, minAngle, maxAgnle);
	}

	void OnGUI() {
		GUILayout.Label("鼠标操作如下：");
		GUILayout.Label("按住鼠标左键，移动鼠标可以视角的旋转");
		GUILayout.Label("鼠标滚轮可以进行缩放");
		GUILayout.Label(" ");
		GUILayout.Label("键盘操作如下：");
		GUILayout.Label("W、A、S、D，对应’前进、后退、左移、右移‘");
		GUILayout.Label("Q、E，对应’上移、下移‘");
	}
}