package main

import (
	"crypto/tls"
	"encoding/xml"
	"errors"
	"flag"
	"io/ioutil"
	"log"
	"net"
	"samsung_aircon_connector/model"
	"strconv"
	"strings"
	"time"

	"github.com/davecgh/go-spew/spew"
	"github.com/gin-gonic/gin"
	"gopkg.in/yaml.v2"
)

var conn *tls.Conn
var cmdCount = 0
var config = model.ConfigType{}
var currentDeviceState = model.DeviceStateRes{}
var isDocker bool = false

func init() {
}

func loadConfig() {
	var path string
	if isDocker {
		log.Println("Docker Mode")
		path = "/config/config.yaml"
	} else {
		log.Println("Local Mode")
		path = "./config/config.yaml"
	}

	file, err := ioutil.ReadFile(path)

	if err != nil {
		log.Fatalf("Config Load Error : %s", err.Error())
	} else {
		err = yaml.Unmarshal(file, &config)
		if err != nil {
			log.Fatalf("Config Unmarshal Error : %s", err.Error())
		}
	}
	spew.Dump(config)
}

func main() {
	flag.BoolVar(&isDocker, "docker", false, "boolean")
	flag.Parse()
	loadConfig()

	router := gin.Default()
	router.GET("/get/:command", requestGetHandler)
	router.GET("/config/:command", requestConfigHandler)
	router.GET("/control/:command/:value", requestControlHandler)
	router.POST("/post", requestPostTest)
	router.Run(":" + config.ServerPort)
}

func requestPostTest(ctx *gin.Context) {
	log.Println("requestPostTest")
	jsonData, _ := ctx.GetRawData()
	log.Println(jsonData)
}

func requestGetHandler(ctx *gin.Context) {
	command := ctx.Param("command")
	response := responseBuilder(command)
	command = strings.ToLower(command)

	switch command {
	case "ping":
		makeResponse(ctx, response, nil)
	case "token":
		message, err := getToken()
		response.Data = message
		makeResponse(ctx, response, err)
	case "devicestate":
		message, err := deviceState()
		response.Data = message
		makeResponse(ctx, response, err)
	default:
		err := errors.New("Unsupported Command")
		makeResponse(ctx, response, err)
		return
	}
}

func requestConfigHandler(ctx *gin.Context) {
	command := ctx.Param("command")
	response := responseBuilder(command)
	command = strings.ToLower(command)

	switch command {
	case "close":
		if conn != nil {
			disconnect()
		}

		makeResponse(ctx, response, nil)
	default:
		err := errors.New("Unsupported Command")
		makeResponse(ctx, response, err)
		return
	}
}

func requestControlHandler(ctx *gin.Context) {
	command := ctx.Param("command")
	response := responseBuilder(command)
	command = strings.ToUpper(command)

	value := ctx.Param("value")

	switch command {
	case "AC_FUN_COMODE":
	case "AC_FUN_DIRECTION":
	case "AC_FUN_OPERATION":
	case "AC_FUN_OPMODE":
	case "AC_FUN_WINDLEVEL":
	case "AC_ADD_AUTOCLEAN":
	case "AC_ADD_SMARTON":
	case "AC_ADD_VOLUME":
	case "AC_FUN_POWER":
		if value == "Toggle" {
			if getValueFromID(ctx, command) == "On" {
				value = "Off"
			} else {
				value = "On"
			}
		}
	case "AC_FUN_TEMPSET":
		var targetTemp int = 18

		if value == "Up" || value == "Down" {
			currentTempValueStr := getValueFromID(ctx, "AC_FUN_TEMPSET")
			currentTempValue, _ := strconv.Atoi(currentTempValueStr)

			if value == "Up" {
				currentTempValue++
			} else if value == "Down" {
				currentTempValue--
			}
			targetTemp = currentTempValue
		} else {
			targetTemp, _ = strconv.Atoi(value)
		}

		if targetTemp < 18 {
			targetTemp = 18
		} else if targetTemp > 30 {
			targetTemp = 30
		}
		value = strconv.Itoa(targetTemp)
	default:
		err := errors.New("Unsupported Command")
		makeResponse(ctx, response, err)
		return
	}

	message, err := deviceContorl(command, value)
	response.Data = message
	makeResponse(ctx, response, err)
}

func getValueFromID(ctx *gin.Context, ID string) string {
	_, err := deviceState()
	if err == nil {
		for _, attr := range currentDeviceState.DeviceState.Device.Attr {
			if attr.ID == ID {
				log.Printf("Current ID : %s / Value : %s", attr.ID, attr.Value)
				return attr.Value
			}
		}
	}
	return ""
}

func responseBuilder(command string) model.ResponseBody {
	return model.ResponseBody{
		Timestamp: time.Now().Format(time.RFC1123Z),
		Command:   command,
	}
}

func getToken() (interface{}, error) {
	err := sendMessage(`<Request Type="GetToken"/>` + "\n")
	if err != nil {
		return "", err
	}

	message, err := logic()
	disconnect()
	return message, err
}

func updateState(message string) error {
	// var attrUpdate = airconUpdate{}
	// _ = xml.Unmarshal([]byte(message), &attrUpdate)
	// var index = 0
	// for index, attr := range currentAirconState.DeviceState.Device.Attr {
	// 	if attr.ID == attrUpdate.Status.Attr.ID {
	// 		log.Printf("Current ID : %s / Value : %s", attr.ID, attr.Value)
	// 		log.Printf("Update ID  : %s / Value : %s", attrUpdate.Status.Attr.ID, attrUpdate.Status.Attr.Value)
	// 		currentAirconState.DeviceState.Device.Attr[index] = attrUpdate.Status.Attr
	// 		return nil
	// 	}
	// }

	// // Error since 0 array reference
	// currentAirconState.DeviceState.Device.Attr[index] = attrUpdate.Status.Attr
	return nil
}

func deviceState() (interface{}, error) {
	err := sendMessage(`<Request Type="DeviceState" duid="` + config.DUID + `"></Request>` + "\n")
	if err != nil {
		return "", err
	}
	return logic()
}

func deviceContorl(command string, value string) (interface{}, error) {
	cmdCount++
	err := sendMessage(`<Request Type="DeviceControl"><Control CommandID="cmd` + strconv.Itoa(cmdCount) + `" duid="` + config.DUID + `"><Attr ID="` + command + `" Value="` + value + `"/></Control></Request>` + "\n")
	if err != nil {
		return "", err
	}
	return logic()
}

func authToken() (interface{}, error) {
	err := sendMessage(`<Request Type="AuthToken"><User Token="` + config.Token + `"/></Request>` + "\n")
	if err != nil {
		return "", err
	}
	return logic()
}

func logic() (interface{}, error) {
	message, err := readMessage()

	if err != nil {
		return message, err
	}

	if strings.Contains(message, `DRC-1.00`) {
		return message, err
	}

	if strings.Contains(message, `Update Type="InvalidateAccount`) {
		// authToken()
		return message, err
	}

	if strings.Contains(message, `Response Type="AuthToken" Status="Okay"`) {
		return message, err
	}

	if strings.Contains(message, `Response Status="Fail" Type="AuthToken" ErrorCode="206"`) {
		return message, errors.New(`Invalid Token`)
	}

	if strings.Contains(message, `Response Type="GetToken" Status="Ready"`) {
		return logic()
	}

	if strings.Contains(message, `Update Type="GetToken" Status="Completed"`) {
		var currentToken = model.Token{}
		err = xml.Unmarshal([]byte(message), &currentToken)
		log.Printf("currentToken : %+v", currentToken)
		return currentToken, err
	}

	if strings.Contains(message, `Response Status="Fail" Type="Authenticate" ErrorCode="301"`) {
		return message, errors.New(message)
	}

	if strings.Contains(message, `Response Type="DeviceState" Status="Okay"`) {
		currentDeviceState = model.DeviceStateRes{}
		err = xml.Unmarshal([]byte(message), &currentDeviceState)
		return currentDeviceState, err
	}

	if strings.Contains(message, `Response Type="DeviceControl" Status="Okay"`) {
		deviceControl := model.DeviceControlRes{}
		err = xml.Unmarshal([]byte(message), &deviceControl)
		return deviceControl, err
	}

	if strings.Contains(message, `Update Type="Status"`) {
		updateState(message)
		return logic()
	}

	if strings.Contains(message, `ErrorCode="103"`) {
		return message, errors.New(`Invali DUID`)
	}

	if strings.Contains(message, `Response Status="Fail"`) {
		return message, errors.New(message)
	}

	return message, errors.New(message)
}

func makeResponse(ctx *gin.Context, response model.ResponseBody, err error) {
	if err != nil {
		response.Status = "Fail"
		response.Data = err.Error()
		ctx.Abort()
		ctx.IndentedJSON(400, response)
	} else {
		response.Status = "Success"
		ctx.Abort()
		ctx.IndentedJSON(200, response)
	}
}

func disconnect() error {
	var err error

	if conn != nil {
		err = conn.Close()
		conn = nil
	}

	return err
}

func connect(retry int) error {
	disconnect()

	conf := &tls.Config{InsecureSkipVerify: true}
	var err error

	for i := 0; i < retry; i++ {
		log.Println(`Conn is nil, Connecting (`, i, `)`)
		conn, err = tls.DialWithDialer(
			&net.Dialer{
				Timeout: time.Second * 30,
			},
			"tcp",
			config.AirconIP+":"+config.AirconPort,
			conf,
		)

		if err == nil {
			break
		}

		time.Sleep(time.Second * 2)
	}

	if err != nil {
		disconnect()
		log.Println("Connect Error: ", err)
		return err
	}

	_, err = logic()

	return err
}

func readMessage() (string, error) {
	log.Println("Read Start")
	buf := make([]byte, 4096)
	n, err := conn.Read(buf)
	message := string(buf[:n])

	if err != nil {
		log.Println("Read Data Error : ", err)
	} else {
		log.Println("Read Data Success : ", message)
	}

	return message, err
}

func sendMessage(message string) error {
	var err error

	if conn != nil {
		_, err = conn.Write([]byte(message))
	}

	if conn == nil || err != nil {
		err = connect(2)

		if err != nil {
			return err
		}

		if strings.Contains(message, `<Request Type="GetToken"/>`) == false {
			_, err = authToken()
			if err != nil {
				disconnect()
				return err
			}
		}
		_, err = conn.Write([]byte(message))
	}

	if err != nil {
		disconnect()
		log.Println("Send Data Fail : ", err)
	} else {
		log.Println("Send Data Success : ", message)
	}

	return err
}
