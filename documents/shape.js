const skill = require('../data/skill.json');
module.exports = (shape) => {
    const {
        name,
        family,
        image,
        landingURL,
        Price
    } = shape;

    return {
        type: 'Alexa.Presentation.APL.RenderDocument',
        token: 'shape-composition',
        "document":
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
                            "textSizeBody": 27,
                            "textSizePrimary": 20,
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
                            "fontWeight": "300",
                            "color": "#414141"
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
                            "fontFamily": "0swald",
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
                    "items": [
                        {
                            "type": "TouchWrapper",
                            "onPress": {
                                "type": "SendEvent",
                                "arguments": ["startEvent"]
                            },
                            "items": [
                                {
                                    "type": "Container",
                                    "direction": "column",
                                    "width": "100vw",
                                    "height": "100vh",
                                    "alignItems": "center",
                                    "justifyContent": "center",
                                    "items": [
                                        {
                                            "type": "Image",
                                            "source": "${payload.data.properties.trueBackgroundImage.sources[0].url}",
                                            "scale": "best-fill",
                                            "position": "absolute",
                                            "width": "100vw",
                                            "height": "100vh"
                                        },
                                        {
                                            "type": "Image",
                                            "source": "${payload.data.properties.backgroundImage.sources[0].url}",
                                            "scale": "best-fit",
                                            "position": "absolute",
                                            "width": "100vw",
                                            "height": "100vh"
                                        },
                                        {
                                            "type": "ScrollView",
                                            "width": "100vw",
                                            "height": "100vh",
                                            "item": [
                                                {
                                                    "type": "Container",
                                                    "direction": "column",
                                                    "alignItems": "center",
                                                    "paddingLeft": 10,
                                                    "paddingRight": 10,
                                                    "paddingTop": 25,
                                                    "paddingBottom": 5,
                                                    "items": [
                                                        {
                                                            "type": "AlexaHeader"
                                                        },
                                                        {
                                                            "type": "Image",
                                                            "source": "${payload.data.properties.image.sources[0].url}",
                                                            "paddingBottom": 80,
                                                            "scale": "best-fit",
                                                            "width": "40vw",
                                                            "height": "40vh",
                                                            "position": "absolute"
                                                        },
                                                        {
                                                            "when": "${@viewportProfile == @hubRoundSmall}",
                                                            "type": "Text",
                                                            "text": "<b>${payload.data.properties.textContent.title.text}</b>",
                                                            "style": "textStyleBody",
                                                            "width": "90vw",
                                                            "paddingTop": 110,
                                                            "textAlign": "center"
                                                        },
                                                        {
                                                            "when": "${@viewportProfile == @hubRoundSmall}",
                                                            "type": "Text",
                                                            "text": "${payload.data.properties.textContent.subtitle.text}",
                                                            "style": "textStylePrimary",
                                                            "width": "90vw",
                                                            "textAlign": "center"
                                                        },
                                                        {
                                                            "when": "${@viewportProfile != @hubRoundSmall}",
                                                            "type": "Text",
                                                            "text": "<b>${payload.data.properties.textContent.title.text}</b>",
                                                            "style": "textStyleBody",
                                                            "width": "90vw",
                                                            "paddingTop": 230,
                                                            "textAlign": "center"
                                                        },
                                                        {
                                                            "when": "${@viewportProfile != @hubRoundSmall}",
                                                            "type": "Text",
                                                            "text": "${payload.data.properties.textContent.subtitle.text}",
                                                            "style": "textStylePrimary",
                                                            "width": "90vw",
                                                            "textAlign": "center"
                                                        }

                                                    ]
                                                }
                                            ]
                                        }
                                    ]
                                },
                                {
                                    "type": "Container",
                                    "width": "100vw",
                                    "height": "100vh",
                                    "direction": "column",
                                    "alignItems": "center",
                                    "justifyContent": "center",
                                    "items": [
                                        {
                                            "type": "Image",
                                            "source": "${payload.data.properties.backgroundImage.sources[0].url}",
                                            "scale": "best-fill",
                                            "width": "100vw",
                                            "height": "100vh",
                                            "position": "absolute"
                                        },
                                        {
                                            "type": "AlexaHeader",
                                            "headerTitle": "${payload.data.properties.title}"
                                        },
                                        {
                                            "type": "Container",
                                            "direction": "row",
                                            "paddingLeft": 40,
                                            "paddingRight": 72,
                                            "grow": 1,
                                            "items": [
                                                {
                                                    "type": "Image",
                                                    "source": "${payload.data.properties.image.sources[0].url}",
                                                    "width": 340,
                                                    "height": 360,
                                                    "scale": "best-fit",
                                                    "align": "center"
                                                },
                                                {
                                                    "type": "ScrollView",
                                                    "height": "60vh",
                                                    "shrink": 1,
                                                    "item": [
                                                        {
                                                            "type": "Container",
                                                            "items": [
                                                                {
                                                                    "type": "Text",
                                                                    "text": "${payload.data.properties.textContent.title.text}",
                                                                    "style": "textStyleBody"
                                                                },
                                                                {
                                                                    "type": "Text",
                                                                    "text": "${payload.data.properties.textContent.subtitle.text}",
                                                                    "style": "textStylePrimary"
                                                                },
                                                                {
                                                                    "type": "Text",
                                                                    "text": "${payload.data.properties.textContent.primaryText.text}",
                                                                    "style": "textStylePrimary"
                                                                }
                                                            ]
                                                        }
                                                    ]
                                                }
                                            ]
                                        }
                                    ]
                                }
                            ]
                        }]
                }
            },
        "datasources": {
            "data": {
                "type": "object",
                "properties": {
                    "trueBackgroundImage": {
                        "contentDescription": null,
                        "smallSourceUrl": null,
                        "largeSourceUrl": null,
                        "sources": [
                            {
                                "url": "https://s3.amazonaws.com/dealsskillassets/Backgrounds_1360x1040-03.png",
                                "size": "small",
                                "widthPixels": 0,
                                "heightPixels": 0
                            },
                            {
                                "url": "https://s3.amazonaws.com/dealsskillassets/Backgrounds_1360x1040-03.png",
                                "size": "large",
                                "widthPixels": 0,
                                "heightPixels": 0
                            }
                        ]
                    },
                    "backgroundImage": {
                        "sources": [
                            {
                                "url": "https://s3.amazonaws.com/dealsskillassets/1360x1040_Transparent-deal.png",
                                "size": "small",
                                "widthPixels": 0,
                                "heightPixels": 0
                            },
                            {
                                "url": "https://s3.amazonaws.com/dealsskillassets/1360x1040_Transparent-deal.png",
                                "size": "large",
                                "widthPixels": 0,
                                "heightPixels": 0
                            }
                        ]
                    },
                    "title": "                 ",
                    "image": {
                        "sources": [
                            {
                                "url": shape.image,
                                "size": "small"
                            }
                        ]
                    },
                    "textContent": {
                        "title": {
                            "type": "PlainText",
                            "text": shape.name
                        },
                        "subtitle": {
                            "type": "PlainText",
                            "text": shape.Price
                        },
                        "primaryText": {
                            "type": "PlainText",
                            "text": ""
                        }
                    },
                    "hintText": "Try saying, \"Tell me a steal\" or say Buy to buy this deal"
                }
            }
        }
    };
};
