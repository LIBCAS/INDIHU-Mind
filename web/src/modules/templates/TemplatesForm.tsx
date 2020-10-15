import React, {
  useState,
  useEffect,
  useContext,
  useRef,
  useCallback
} from "react";
import { FormikProps, Form, Field, FieldProps } from "formik";
import Typography from "@material-ui/core/Typography";
import Button from "@material-ui/core/Button";
import classNames from "classnames";
import Add from "@material-ui/icons/Add";

import { notEmpty } from "../../utils/form/validate";
import { templateGet } from "../../context/actions/template";
import { GlobalContext } from "../../context/Context";
import {
  CardTemplateProps,
  CardTemplateAttribute
} from "../../types/cardTemplate";
import { Formik } from "../../components/form/Formik";
import { Loader } from "../../components/loader/Loader";
import { InputText } from "../../components/form/InputText";
import { Divider } from "../../components/divider/Divider";
import { MessageSnackbar } from "../../components/messages/MessageSnackbar";
import { Popover } from "../../components/portal/Popover";
import { ButtonGrey } from "../../components/control/ButtonGrey";

import { TemplatesAttribute } from "./TemplatesAttribute";
import { TemplatesAddAttribute } from "./TemplatesAddAttribute";

import { useStyles } from "./_templatesStyles";
import { useStyles as useTextStyles } from "../../theme/styles/textStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";

import { onSubmitTemplate } from "./_utils";

export interface TemplatesFormValues {
  id: string;
  name: string;
  attributeTemplates: CardTemplateAttribute[];
}

const initialValues = {
  id: "",
  name: "",
  attributeTemplates: []
};

interface TemplatesFormProps {
  setShowModal: Function;
  template?: CardTemplateProps;
}

export const TemplatesForm: React.FC<TemplatesFormProps> = ({
  setShowModal,
  template
}) => {
  const classes = useStyles();
  const classesText = useTextStyles();
  const classesSpacing = useSpacingStyles();
  const classesLayout = useLayoutStyles();
  const context: any = useContext(GlobalContext);
  const dispatch: Function = context.dispatch;
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<boolean>(false);
  const [initValues, setInitValues] = useState<TemplatesFormValues>(
    initialValues
  );
  const AddAttributeRef = useRef(null);
  const [popoverOpen, setPopoverOpen] = useState<boolean>(false);
  const onAddAttribute = useCallback(() => setPopoverOpen(true), []);

  useEffect(() => {
    if (template) {
      const { id, name, attributeTemplates } = template;
      setInitValues({
        id,
        name,
        attributeTemplates
      });
    }
  }, [template]);

  return (
    <>
      <Loader loading={loading} />
      {error && <MessageSnackbar setVisible={setError} />}
      <Formik
        initialValues={initValues}
        enableReinitialize
        onSubmit={(values: TemplatesFormValues) => {
          if (loading) return;
          setLoading(true);
          onSubmitTemplate(
            values,
            setShowModal,
            setError,
            setLoading,
            templateGet,
            dispatch,
            template
          );
        }}
        render={(formikBag: FormikProps<TemplatesFormValues>) => {
          return (
            <Form>
              <div
                className={classNames(
                  classesLayout.flex,
                  classesLayout.flexWrap,
                  classesLayout.justifyCenter,
                  classesLayout.directionColumn,
                  classesSpacing.ml2,
                  classesSpacing.mr2
                )}
              >
                <Field
                  name="name"
                  validate={notEmpty}
                  render={({
                    field,
                    form
                  }: FieldProps<TemplatesFormValues>) => (
                    <InputText
                      label="Název"
                      type="text"
                      field={field}
                      form={form}
                      autoFocus={template ? false : true}
                    />
                  )}
                />
                <Typography
                  className={classNames(
                    classesText.subtitle,
                    classesSpacing.mt2
                  )}
                >
                  ATRIBUTY
                </Typography>
                {formikBag.values.attributeTemplates.map(
                  (att: CardTemplateAttribute) => {
                    return (
                      <TemplatesAttribute
                        key={att.id}
                        attribute={att}
                        formikBag={formikBag}
                      />
                    );
                  }
                )}
                <div ref={AddAttributeRef} className={classes.addWrapper}>
                  <ButtonGrey
                    text="Přidat atribut"
                    onClick={onAddAttribute}
                    bold
                    Icon={<Add fontSize="small" />}
                  />
                  <Popover
                    open={popoverOpen}
                    setOpen={setPopoverOpen}
                    anchorEl={AddAttributeRef.current}
                    content={
                      <TemplatesAddAttribute
                        formikBagParent={formikBag}
                        setOpen={setPopoverOpen}
                      />
                    }
                  />
                </div>
              </div>
              <Divider className={classesSpacing.mt3} />
              <div
                className={classNames(
                  classesLayout.flex,
                  classesLayout.justifyCenter,
                  classesSpacing.mb1
                )}
              >
                <Button
                  className={classesSpacing.mt3}
                  variant="contained"
                  color="primary"
                  type="submit"
                >
                  {template ? "Změnit šablonu" : "Vytvořit šablonu"}
                </Button>
              </div>
            </Form>
          );
        }}
      />
    </>
  );
};
