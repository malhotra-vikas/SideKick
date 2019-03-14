const skill = require('../data/skill.json');

module.exports = () => {
    return {
        type: "Alexa.Presentation.APL.RenderDocument",
        token: "launch-screen",
        document:
            {
                "type": "APL",
                "version": "1.0",
                "theme": "light",
                "import": [
                    {
                        "name": "alexa-styles",
                        "version": "1.0.0-beta"
                    },
                    {
                        "name": "alexa-layouts",
                        "version": "1.0.0"
                    }
                ],
                "resources": [
                    {
                        "description": "Stock color for the light theme",
                        "colors": {
                            "colorTextPrimary": "#414141"
                        }
                    },
                    {
                        "description": "Stock color for the dark theme",
                        "when": "${viewport.theme == 'dark'}",
                        "colors": {
                            "colorTextPrimary": "#414141"
                        }
                    },
                    {
                        "description": "Standard font sizes",
                        "dimensions": {
                            "textSizeBody": 48,
                            "textSizePrimary": 27,
                            "textSizeSecondary": 23,
                            "textSizeSecondaryHint": 25
                        }
                    },
                    {
                        "description": "Common spacing values",
                        "dimensions": {
                            "spacingThin": 6,
                            "spacingSmall": 12,
                            "spacingMedium": 24,
                            "spacingLarge": 48,
                            "spacingExtraLarge": 72
                        }
                    },
                    {
                        "description": "Common margins and padding",
                        "dimensions": {
                            "marginTop": 40,
                            "marginLeft": 60,
                            "marginRight": 60,
                            "marginBottom": 40
                        }
                    }
                ],
                "styles": {
                    "textStyleBase": {
                        "description": "Base font description; set color and core font family",
                        "values": [
                            {
                                "color": "@colorTextPrimary",
                                "fontFamily": "Oswald"
                            }
                        ]
                    },
                    "textStyleBase0": {
                        "description": "Thin version of basic font",
                        "extend": "textStyleBase",
                        "values": {
                            "fontWeight": "100"
                        }
                    },
                    "textStyleBase1": {
                        "description": "Light version of basic font",
                        "extend": "textStyleBase",
                        "values": {
                            "fontWeight": "300"
                        }
                    },
                    "mixinBody": {
                        "values": {
                            "fontSize": "@textSizeBody"
                        }
                    },
                    "mixinPrimary": {
                        "values": {
                            "fontSize": "@textSizePrimary"
                        }
                    },
                    "mixinSecondary": {
                        "values": {
                            "fontSize": "@textSizeSecondary"
                        }
                    },
                    "textStylePrimary": {
                        "extend": [
                            "textStyleBase1",
                            "mixinPrimary"
                        ]
                    },
                    "textStyleSecondary": {
                        "extend": [
                            "textStyleBase0",
                            "mixinSecondary"
                        ]
                    },
                    "textStyleBody": {
                        "extend": [
                            "textStyleBase1",
                            "mixinBody"
                        ]
                    },
                    "textStyleSecondaryHint": {
                        "values": {
                            "fontFamily": "Oswald",
                            "fontStyle": "italic",
                            "fontSize": "@textSizeSecondaryHint",
                            "color": "@colorTextPrimary"
                        }
                    }
                },
                "layouts": {},
                "mainTemplate": {
                    "parameters": [
                        "payload"
                    ],
                    "item": {
                        "type": "TouchWrapper",
                        "width": "100%",
                        "height": "100%",
                        "inheritParentState": true,
                        "onPress": {
                            "type": "SendEvent",
                            "arguments": ["startEvent"]
                        },
                        "items": [
                            {
                                "type": "Container",
                                "direction": "column",
                                "items": [
                                    {
                                        "type": "Image",
                                        "source": "${payload.data.properties.backgroundImage.sources[0].url}",
                                        "scale": "best-fit",
                                        "position": "absolute",
                                        "width": "100vw",
                                        "height": "100vh",
                                        "align": "center"
                                    }
                                ]
                            },
                            {
                                "type": "Container",
                                "items": [
                                    {
                                        "when": "${@viewportProfile == @hubRoundSmall}",
                                        "type": "Image",
                                        "source": "${payload.data.properties.backgroundImage.sources[0].url}",
                                        "scale": "best-fit",
                                        "position": "absolute",
                                        "width": "100vw",
                                        "height": "100vh",
                                        "align": "center"
                                    },
                                    {
                                        "when": "${@viewportProfile != @hubRoundSmall}",
                                        "type": "Image",
                                        "source": "${payload.data.properties.backgroundImage.sources[0].url}",
                                        "scale": "best-fit",
                                        "position": "absolute",
                                        "width": "100vw",
                                        "height": "100vh"
                                    },
                                    {
                                        "type": "Container",
                                        "direction": "row",
                                        "paddingLeft": "5vw",
                                        "paddingRight": "5vw",
                                        "paddingBottom": "5vh",
                                        "alignItems": "center",
                                        "justifyContent": "center",
                                        "items": [
                                            {
                                                "when": "${@viewportProfile == @hubRoundSmall}",
                                                "type": "Image",
                                                "height": "100vh",
                                                "width": "100vw",
                                                "source": "${payload.data.properties.image.sources[0].url}",
                                                "scale": "best-fit",
                                                "align": "center"
                                            },
                                            {
                                                "when": "${@viewportProfile != @hubRoundSmall}",
                                                "type": "Image",
                                                "height": "100vh",
                                                "width": "100vw",
                                                "source": "${payload.data.properties.image.sources[0].url}",
                                                "align": "center"
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    }
                }
            },
        datasources:
            {
                "data": {
                    "type": "object",
                    "properties": {
                        "title": "                 ",
                        "backgroundImage": {
                            "contentDescription": null,
                            "smallSourceUrl": "https://s3.amazonaws.com/dealsskillassets/Backgrounds_1360x1040-03.png",
                            "largeSourceUrl": "https://s3.amazonaws.com/dealsskillassets/Backgrounds_1360x1040-03.png",
                            "sources": [
                                {
                                    "url": "https://s3.amazonaws.com/dealsskillassets/Backgrounds_1360x1040-03.png",
                                    "size": "large",
                                    "widthPixels": 0,
                                    "heightPixels": 0
                                }
                            ]
                        },
                        "image": {
                            "contentDescription": null,
                            "smallSourceUrl": null,
                            "largeSourceUrl": null,
                            "sources": [
                                {
                                    "url": "https://s3.amazonaws.com/dealsskillassets/1360x1040_Transparent-main.png",
                                    "size": "large",
                                    "widthPixels": 0,
                                    "heightPixels": 0
                                },
                                {
                                    "url": "https://s3.amazonaws.com/dealsskillassets/255x195_Transparent-main.png",
                                    "size": "small",
                                    "widthPixels": 0,
                                    "heightPixels": 0
                                }
                            ]
                        },
                        "showimage": {
                            "contentDescription": null,
                            "smallSourceUrl": null,
                            "largeSourceUrl": null,
                            "sources": [
                                {
                                    "url": "https://s3.amazonaws.com/dealsskillassets/2134_main.png",
                                    "size": "large",
                                    "widthPixels": 0,
                                    "heightPixels": 0
                                },
                                {
                                    "url": "https://s3.amazonaws.com/dealsskillassets/2134_main.png",
                                    "size": "small",
                                    "widthPixels": 0,
                                    "heightPixels": 0
                                }
                            ]
                        },
                        "hintText": "Try saying, \"Tell me a steal\""
                    }
                }
            }
    }
    };
