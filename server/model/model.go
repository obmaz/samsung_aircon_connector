package model

type ConfigType struct {
	ServerPort string `yaml:"ServerPort"`
	AirconIP string `yaml:"AirconIP"`
	AirconPort string `yaml:"AirconPort"`
	Token      string `yaml:"Token"`
	DUID       string `yaml:"DUID"`
}

type ResponseBody struct {
	Timestamp string      `json:"timestamp"`
	Command   string      `json:"command"`
	Status    string      `json:"status"`
	Data      interface{} `json:"data"`
}

type Token struct {
	Token string `xml:"Token,attr" json:"token"`
}

type DeviceStateRes struct {
	Type        string      `xml:"Type,attr" json:"type"`
	Status      string      `xml:"Status,attr" json:"status"`
	DeviceState deviceState `json:"deviceState"`
}

type deviceState struct {
	Device device `json:"device"`
}

type device struct {
	DUID    string `xml:"DUID,attr" json:"duid"`
	GroupID string `xml:"GroupID,attr" json:"groupId"`
	ModelID string `xml:"ModelID,attr" json:"modelId"`
	Attr    []attr `json:"attr"`
}

type UpdateRes struct {
	Type   string `xml:"Type,attr" json:"type"`
	Status status `json:"status"`
}

type status struct {
	DUID    string `xml:"DUID,attr" json:"duid"`
	GroupID string `xml:"GroupID,attr" json:"groupId"`
	ModelID string `xml:"ModelID,attr" json:"modelId"`
	Attr    attr   `json:"attr"`
}

type attr struct {
	ID    string `xml:"ID,attr" json:"id"`
	Type  string `xml:"Type,attr" json:"type"`
	Value string `xml:"Value,attr" json:"value"`
}

type DeviceControlRes struct {
	Type      string `xml:"Type,attr" json:"type"`
	Status    string `xml:"Status,attr" json:"status"`
	DUID      string `xml:"DUID,attr" json:"duid"`
	CommandID string `xml:"CommandID,attr" json:"commandId"`
}
